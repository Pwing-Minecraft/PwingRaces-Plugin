package net.pwing.races.race;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.pwing.races.PwingRaces;
import net.pwing.races.config.RaceConfiguration;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.race.ability.RaceAbilityManager;
import net.pwing.races.race.attribute.RaceAttributeManager;
import net.pwing.races.race.leveling.RaceLevelManager;
import net.pwing.races.race.permission.RacePermissionManager;
import net.pwing.races.race.skilltree.RaceSkilltreeManager;
import net.pwing.races.race.trigger.RaceTriggerManager;

public class PwingRaceManager implements RaceManager {

    private PwingRaces plugin;

    private RaceTriggerManager triggerManager;
    private RaceAttributeManager attributeManager;
    private RacePermissionManager permissionManager;
    private RaceLevelManager levelManager;
    private RaceAbilityManager abilityManager;
    private RaceSkilltreeManager skilltreeManager;

    private RaceMenu raceMenu;

    private Set<Race> races;
    private Map<UUID, RacePlayer> racePlayers;

    public PwingRaceManager(PwingRaces plugin) {
        this.plugin = plugin;

        initRaces();
    }

    public void initRaces() {
        Bukkit.getServer().getPluginManager().registerEvents(new RaceListener(plugin), plugin);

        races = new HashSet<Race>();
        racePlayers = new HashMap<UUID, RacePlayer>();

        triggerManager = new RaceTriggerManager(plugin);
        attributeManager = new RaceAttributeManager(plugin);
        permissionManager = new RacePermissionManager(plugin);
        levelManager = new RaceLevelManager(plugin);
        abilityManager = new RaceAbilityManager(plugin);
        skilltreeManager = new RaceSkilltreeManager(new File(plugin.getDataFolder(), "skilltrees"));

        for (RaceConfiguration config : plugin.getConfigManager().getRaceConfigurations())
            races.add(new PwingRace(this, config.getConfig()));

        FileConfiguration config = plugin.getConfig();
        raceMenu = new RaceMenu(plugin, config.getString("menu.name", "Race Selection"), config.getInt("menu.slots", 45), config.getBoolean("menu.glass-filled", false));
    }

    public boolean setupPlayer(OfflinePlayer player) {
        RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getUniqueId());
        if (playerConfig == null) {
            plugin.getLogger().severe("Could not create player data file for " + player.getName() + "!");
            return false;
        }

        String raceName = playerConfig.getConfig().getString("active-race");
        if (raceName == null || raceName.isEmpty())
            raceName = plugin.getConfigManager().getDefaultRace();

        Map<String, RaceData> raceDataMap = new HashMap<String, RaceData>();
        for (Race race : races)
            raceDataMap.put(race.getName(), new PwingRaceData(race.getName(), "data", playerConfig));

        Race activeRace = getRaceFromName(raceName);
        if (activeRace == null && plugin.getConfigManager().doesRequireRace()) {
            plugin.getLogger().severe("Could not find race " + raceName + ", please check the data config for " + player.getName() + "!");
        }

        racePlayers.put(player.getUniqueId(), new PwingRacePlayer(player, activeRace, raceDataMap));
        return true;
    }

    protected void runSetupTask(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            RacePlayer racePlayer = getRacePlayer(player);
            if (racePlayer == null) {
                plugin.getLogger().severe("Could not find or create race player data for player " + player.getName() + "!");
                return;
            }

            Race race = racePlayer.getActiveRace();
            if (race == null)
                return;

            RaceData raceData = getRacePlayer(player).getRaceData(race);
            if (plugin.getConfigManager().sendSkillpointMessageOnJoin()) {
                player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("skillpoint-amount-message", "%prefix% &aYou have %skillpoints% unused skillpoints.").replace("%skillpoints%", String.valueOf(raceData.getUnusedSkillpoints()))));
            }
        }, 20);
    }

    public void savePlayer(Player player) {
        RacePlayer racePlayer = getRacePlayer(player);
        if (racePlayer == null) {
            plugin.getLogger().severe("Could not save player " + player.getName() + "! An error may have occurred when this player first joined.");
            return;
        }

        RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getUniqueId());

        YamlConfiguration config = playerConfig.getConfig();
        String raceName = plugin.getConfigManager().getDefaultRace();
        Race activeRace = racePlayer.getActiveRace();
        if (activeRace != null) {
            raceName = activeRace.getName();
        }
        config.set("active-race", raceName);
        for (Race race : races) {
            RaceData data = racePlayer.getRaceData(race);
            config.set("data." + race.getName() + ".unlocked", data.isUnlocked());
            config.set("data." + race.getName() + ".level", data.getLevel());
            config.set("data." + race.getName() + ".exp", data.getExperience());
            config.set("data." + race.getName() + ".used-skillpoints", data.getUsedSkillpoints());
            config.set("data." + race.getName() + ".unused-skillpoints", data.getUnusedSkillpoints());

            for (String str : race.getSkilltreeMap().values()) {
                List<String> elements = data.getPurchasedElements(str);
                config.set("data." + race.getName() + ".purchased-elements." + str, elements);
            }
        }

        playerConfig.saveConfig();
    }

    public Race getRaceFromName(String name) {
        for (Race race : races) {
            if (race.getName().equalsIgnoreCase(name))
                return race;
        }

        return null;
    }

    public RacePlayer getRacePlayer(OfflinePlayer player) {
        return racePlayers.get(player.getUniqueId());
    }

    public RaceData getPlayerData(OfflinePlayer player, Race race) {
        return racePlayers.get(player.getUniqueId()).getRaceData(race);
    }

    public void registerPlayer(Player player) {
        registerPlayer(player, false);
    }

    public void registerPlayer(Player player, boolean override) {
        RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getUniqueId());

        YamlConfiguration config = playerConfig.getConfig();
        if (!config.contains("active-race") || override) {
            boolean hasDefaultRace = plugin.getConfigManager().hasDefaultRaceOnJoin();

            if (hasDefaultRace) {
                Race defaultRace = getRaceFromName(plugin.getConfigManager().getDefaultRace());
                if (defaultRace == null) {
                    plugin.getLogger().severe("Could not find default race " + plugin.getConfigManager().getDefaultRace() + "! Please make sure your config is correct!");
                } else {
                    config.set("active-race", plugin.getConfigManager().getDefaultRace());
                    defaultRace.getRaceItems().values().forEach(item -> player.getInventory().addItem(item));
                }
            }
        }

        for (Race race : races) {
            String racePath = "data." + race.getName();

            if (!config.contains(racePath) || override) {
                config.set(racePath + ".level", 1);
                config.set(racePath + ".unused-skillpoints", race.getSkillpointsForLevel(1));
                config.set(racePath + ".used-skillpoints", 0);
                config.set(racePath + ".exp", 0);
                config.set(racePath + ".unlocked", !race.doesRequireUnlock());
            }

            for (String str : race.getSkilltreeMap().values()) {
                if (!config.contains(racePath + ".purchased-elements." + str) || override)
                    config.set(racePath + ".purchased-elements." + str, new ArrayList<String>());

            }
        }

        playerConfig.saveConfig();
    }

    public RaceTriggerManager getTriggerManager() {
        return triggerManager;
    }

    public RaceAttributeManager getAttributeManager() {
        return attributeManager;
    }

    public RacePermissionManager getPermissionManager() {
        return permissionManager;
    }

    public RaceLevelManager getLevelManager() {
        return levelManager;
    }

    public RaceAbilityManager getAbilityManager() {
        return abilityManager;
    }

    public RaceSkilltreeManager getSkilltreeManager() {
        return skilltreeManager;
    }

    public RaceMenu getRacesMenu() {
        return raceMenu;
    }

    public boolean isRacesEnabledInWorld(World world) {
        RaceConfigurationManager configManager = plugin.getConfigManager();
        if (configManager.getDisabledWorlds().contains(world.getName()))
            return false;

        return true;
    }

    public Set<Race> getRaces() {
        return races;
    }

    public Map<UUID, RacePlayer> getRacePlayerMap() {
        return racePlayers;
    }
}