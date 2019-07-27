package net.pwing.races.race.ability;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.api.race.ability.RaceAbilityManager;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.utilities.MessageUtil;
import net.pwing.races.utilities.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PwingRaceAbilityManager implements RaceAbilityManager {

    private PwingRaces plugin;
    private Map<String, Map<UUID, Long>> cooldown = new HashMap<String, Map<UUID, Long>>();

    public PwingRaceAbilityManager(PwingRaces plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new RaceAbilityListener(plugin), plugin);
    }

    public void runAbilities(Player player) {
        Race race = plugin.getRaceManager().getRacePlayer(player).getActiveRace();
        if (race == null)
            return;

        Collection<RaceAbility> raceAbilities = getApplicableAbilities(player, race);
        if (raceAbilities == null || raceAbilities.isEmpty())
            return;

        for (RaceAbility ability : raceAbilities) {
            runAbility(player, ability);
        }
    }

    public boolean runAbility(Player player, RaceAbility ability) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld()))
            return false;

        if (!ability.getRequiredPermission().equals("none") && !plugin.getVaultHook().hasPermission(player, "pwingraces.ability." + ability.getRequiredPermission()) && !plugin.getVaultHook().hasPermission(player, "pwingraces.ability.*"))
            return false;

        if (hasCooldown(player, ability.getInternalName())) {
            String cooldown = TimeUtil.getTime(getCooldown(player, ability.getInternalName()) + 1);
            String cooldownMessage = ability.getCooldownMessage();

            if (cooldownMessage == null)
                cooldownMessage = MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("ability-cooldown", "%prefix% &cThis ability is currently on cooldown (%time% remaining)."));

            if (!cooldownMessage.equalsIgnoreCase("none"))
                player.sendMessage(cooldownMessage.replace("%time%", cooldown));

            return ability.isDefaultActionOverriden();
        }

        if (ability.runAbility(player)) {
            setCooldown(player, ability.getInternalName(), ability.getCooldown());

            RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
            triggerManager.runTriggerPassives(player, ability.getPassives());
            return true;
        }

        return ability.isDefaultActionOverriden();
    }

    public Collection<RaceAbility> getApplicableAbilities(Player player, Race race) {
        RaceManager raceManager = plugin.getRaceManager();
        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceAbility> abilities = new HashMap<String, RaceAbility>();
        for (String key : race.getRaceAbilitiesMap().keySet()) {
            List<RaceAbility> definedAbilities = race.getRaceAbilitiesMap().get(key);

            for (RaceAbility definedAbility : definedAbilities) {
                String req = definedAbility.getRequirement();

                if (req.equals("none")) {
                    abilities.put(definedAbility.getInternalName(), definedAbility);

                } else if (req.startsWith("level")) { // best to assume it's a level-based ability
                    int level = Integer.parseInt(req.replace("level", ""));

                    if (data.getLevel() < level)
                        continue;

                    abilities.put(definedAbility.getInternalName(), definedAbility);
                } else {
                    for (String str : race.getSkilltreeMap().values()) {
                        RaceSkilltree skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(str);
                        if (data.hasPurchasedElement(skilltree.getInternalName(), req)) {
                            abilities.put(definedAbility.getInternalName(), definedAbility);
                        }
                    }
                }
            }
        }

        List<String> toRemove = new ArrayList<String>();
        for (String str : abilities.keySet()) {
            RaceAbility raceAbility = abilities.get(str);

            if (!raceAbility.getInternalName().equalsIgnoreCase(str)) {
                toRemove.add(str);
            }

            if (raceAbility.getAllowedWorlds() != null && !raceAbility.getAllowedWorlds().isEmpty()) {
                if (!raceAbility.getAllowedWorlds().contains(player.getWorld().getName())) {
                    toRemove.add(str);
                }
            }
        }

        toRemove.forEach(removed -> abilities.remove(removed));
        return abilities.values();
    }

    public boolean hasCooldown(Player player, String ability) {
        if (!cooldown.containsKey(ability))
            return false;

        if (!cooldown.get(ability).containsKey(player.getUniqueId()))
            return false;

        if (cooldown.get(ability).get(player.getUniqueId()) < System.currentTimeMillis())
            return false;

        return true;
    }

    public int getCooldown(Player player, String ability) {
        if (!cooldown.containsKey(ability))
            return 0;

        if (!cooldown.get(ability).containsKey(player.getUniqueId()))
            return 0;

        int remainingTime = Math.round(cooldown.get(ability).get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
        return remainingTime;
    }

    public void setCooldown(Player player, String ability, double amt) {
        Map<UUID, Long> cooldownMap = new HashMap<UUID, Long>();

        if (cooldown.containsKey(ability))
            cooldownMap = cooldown.get(ability);

        long time = (long) (amt * 1000) + System.currentTimeMillis();

        cooldownMap.put(player.getUniqueId(), time);
        cooldown.put(ability, cooldownMap);
    }

    public RaceAbility getAbility(String key, String requirement, String configPath, FileConfiguration config) {
        String abilityClassName = config.getString(configPath + ".ability", "DummyAbility");
        Class<? extends RaceAbility> abilityClass = null;

        File folder = plugin.getModuleFolder();
        if (!folder.exists())
            folder.mkdir();

        List<File> modules = new ArrayList<File>();
        for (File module : folder.listFiles()) {
            if (module.getName().endsWith(".jar"))
                modules.add(module);
        }

        URL[] urls = new URL[modules.size() + 1];
        ClassLoader classLoader = plugin.getPluginClassLoader();
        try {
            urls[0] = folder.toURI().toURL();
            for (int i = 1; i <= modules.size(); i++)
                urls[i] = modules.get(i - 1).toURI().toURL();

            classLoader = new URLClassLoader(urls, plugin.getPluginClassLoader());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            abilityClass = classLoader.loadClass("net.pwing.races.race.ability.abilities." + abilityClassName).asSubclass(RaceAbility.class);
        } catch (ClassNotFoundException ex) {
            try {
                // Assume it's a custom ability and the path is defined
                if (abilityClassName.contains("")) {
                    plugin.getLogger().info("Loading custom ability " + abilityClassName);
                    abilityClass = classLoader.loadClass(abilityClassName).asSubclass(RaceAbility.class);
                } else {
                    plugin.getLogger().warning("Attempted to find ability with name " + abilityClassName + ", but nothing was found.");
                    return null;
                }

                plugin.getLogger().info("Successfully loaded custom ability module " + abilityClassName + "!");
            } catch (ClassNotFoundException e2) {
                plugin.getLogger().warning("Attempted to find custom ability with class path " + abilityClassName + ", but nothing was found.");
                return null;
            }
        }

        if (abilityClass == null) {
            plugin.getLogger().warning("Attempted to find ability with name " + abilityClassName + ", but nothing was found.");
            return null;
        }

        try {
            Constructor<? extends RaceAbility> abilityConstructor = abilityClass.getConstructor(PwingRaces.class, String.class, String.class, FileConfiguration.class, String.class);
            abilityConstructor.setAccessible(true);

            RaceAbility ability = abilityConstructor.newInstance(plugin, key, configPath, config, requirement);
            // RaceAbility implements listener, so no need to check if its assignable
            plugin.getServer().getPluginManager().registerEvents(ability, plugin);

            return ability;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            plugin.getLogger().warning("Could not load ability " + abilityClassName + ", please make sure everything in your config is correct.");
            ex.printStackTrace();
        } finally {
            if (classLoader instanceof URLClassLoader) {
                try {
                    ((URLClassLoader) classLoader).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
