package net.pwing.races.race.ability;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.api.race.ability.RaceAbilityManager;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.MessageUtil;
import net.pwing.races.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PwingRaceAbilityManager implements RaceAbilityManager {

    private PwingRaces plugin;
    private Map<String, Map<UUID, Long>> cooldown = new HashMap<>();

    public PwingRaceAbilityManager(PwingRaces plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new RaceAbilityListener(plugin), plugin);
    }

    public void runAbilities(Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        Collection<RaceAbility> raceAbilities = getApplicableAbilities(player, race);
        if (raceAbilities.isEmpty())
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

        if (plugin.getConfigManager().isDisableAbilitiesInCreative() && player.getGameMode() == GameMode.CREATIVE)
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

        for (Map.Entry<String, Collection<RaceCondition>> fullCondition : ability.getConditions().entrySet()) {
            String[] conditionArgs = fullCondition.getKey().split(" ");
            String[] conditionNoArgs = fullCondition.getKey().substring(1).split(" ");
            for (RaceCondition condition : fullCondition.getValue()) {
                if (fullCondition.getKey().startsWith("!")) {
                    if (condition.check(player, conditionNoArgs))
                        return false;
                } else {
                    if (!condition.check(player, conditionArgs))
                        return false;
                }
            }
        }

        if (ability.runAbility(player)) {
            setCooldown(player, ability.getInternalName(), ability.getCooldown());
            runPassives(player, ability);
            return true;
        }

        return ability.isDefaultActionOverriden();
    }

    public Collection<RaceAbility> getApplicableAbilities(Player player, Race race) {
        RaceManager raceManager = plugin.getRaceManager();
        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceAbility> abilities = new HashMap<>();
        for (Map.Entry<String, List<RaceAbility>> entry : race.getRaceAbilitiesMap().entrySet()) {
            List<RaceAbility> definedAbilities = entry.getValue();

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
                        Optional<RaceSkilltree> skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(str);
                        if (!skilltree.isPresent())
                            continue;

                        if (data.hasPurchasedElement(skilltree.get().getInternalName(), req)) {
                            abilities.put(definedAbility.getInternalName(), definedAbility);
                        }
                    }
                }
            }
        }

        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, RaceAbility> entry : abilities.entrySet()) {
            String key = entry.getKey();
            RaceAbility raceAbility = entry.getValue();
            if (!raceAbility.getInternalName().equalsIgnoreCase(key)) {
                toRemove.add(key);
            }

            if (raceAbility.getAllowedWorlds() != null && !raceAbility.getAllowedWorlds().isEmpty()) {
                if (!raceAbility.getAllowedWorlds().contains(player.getWorld().getName())) {
                    toRemove.add(key);
                }
            }
        }

        toRemove.forEach(abilities::remove);
        return abilities.values();
    }

    public boolean hasCooldown(Player player, String ability) {
        if (!cooldown.containsKey(ability))
            return false;

        if (!cooldown.get(ability).containsKey(player.getUniqueId()))
            return false;

        return cooldown.get(ability).get(player.getUniqueId()) >= System.currentTimeMillis();
    }

    public int getCooldown(Player player, String ability) {
        if (!cooldown.containsKey(ability))
            return 0;

        if (!cooldown.get(ability).containsKey(player.getUniqueId()))
            return 0;

        return Math.round(cooldown.get(ability).get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    public void setCooldown(Player player, String ability, double amt) {
        Map<UUID, Long> cooldownMap = new HashMap<>();

        if (cooldown.containsKey(ability))
            cooldownMap = cooldown.get(ability);

        long time = (long) (amt * 1000) + System.currentTimeMillis();

        cooldownMap.put(player.getUniqueId(), time);
        cooldown.put(ability, cooldownMap);
    }

    @Override
    public void runPassives(Player player, RaceAbility ability) {
        ability.getPassives().keySet().forEach(fullPassive -> ability.getPassives().get(fullPassive).forEach(passive -> passive.runPassive(player, fullPassive)));
    }

    public Optional<RaceAbility> getAbility(String key, String requirement, String configPath, FileConfiguration config) {
        String abilityClassName = config.getString(configPath + ".ability", "DummyAbility");
        Class<? extends RaceAbility> abilityClass;

        try {
            abilityClass = Class.forName("net.pwing.races.race.ability.abilities." + abilityClassName).asSubclass(RaceAbility.class);
        } catch (ClassNotFoundException ex) {
            try {
                // Assume it's a custom ability and the path is defined
                if (abilityClassName.isEmpty()) {
                    plugin.getLogger().warning("Attempted to find ability with name " + abilityClassName + ", but nothing was found.");
                    return Optional.empty();
                }
                abilityClass = Class.forName(abilityClassName).asSubclass(RaceAbility.class);
            } catch (ClassNotFoundException ex2) {
                plugin.getLogger().warning("Attempted to find custom ability with class path " + abilityClassName + ", but nothing was found.");
                return Optional.empty();
            }
        }

        try {
            Constructor<? extends RaceAbility> abilityConstructor = abilityClass.getConstructor(PwingRaces.class, String.class, String.class, FileConfiguration.class, String.class);
            abilityConstructor.setAccessible(true);

            RaceAbility ability = abilityConstructor.newInstance(plugin, key, configPath, config, requirement);
            // RaceAbility implements listener, so no need to check if its assignable
            plugin.getServer().getPluginManager().registerEvents(ability, plugin);
            return Optional.of(ability);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            plugin.getLogger().warning("Could not load ability " + abilityClassName + ", please make sure everything in your config is correct.");
            ex.printStackTrace();
        }

        return Optional.empty();
    }
}
