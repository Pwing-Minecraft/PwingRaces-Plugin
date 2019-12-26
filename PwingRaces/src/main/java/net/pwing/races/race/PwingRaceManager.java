package net.pwing.races.race;

import lombok.Getter;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.ability.RaceAbilityManager;
import net.pwing.races.api.race.attribute.RaceAttributeManager;
import net.pwing.races.api.race.level.RaceLevelManager;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.api.race.permission.RacePermissionManager;
import net.pwing.races.api.race.skilltree.RaceSkilltreeManager;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.config.RaceConfiguration;
import net.pwing.races.race.ability.PwingRaceAbilityManager;
import net.pwing.races.race.attribute.PwingRaceAttributeManager;
import net.pwing.races.race.leveling.PwingRaceLevelManager;
import net.pwing.races.race.menu.PwingRaceMenu;
import net.pwing.races.race.permission.PwingRacePermissionManager;
import net.pwing.races.race.skilltree.PwingRaceSkilltreeManager;
import net.pwing.races.race.trigger.PwingRaceTriggerManager;
import net.pwing.races.util.item.ItemUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
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

        races = new HashSet<>();
        racePlayers = new HashMap<>();

        triggerManager = new PwingRaceTriggerManager(plugin);
        attributeManager = new PwingRaceAttributeManager(plugin);
        permissionManager = new PwingRacePermissionManager(plugin);
        levelManager = new PwingRaceLevelManager(plugin);
        abilityManager = new PwingRaceAbilityManager(plugin);
        skilltreeManager = new PwingRaceSkilltreeManager(new File(plugin.getDataFolder(), "skilltrees"));

        for (RaceConfiguration config : plugin.getConfigManager().getRaceConfigs())
            races.add(new PwingRace(this, config.getConfig()));

        FileConfiguration config = plugin.getConfig();
        raceMenu = new PwingRaceMenu(plugin, config.getString("menu.name", "Race Selection"), config.getInt("menu.slots", 45), config.getBoolean("menu.glass-filled", false));
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

        Map<String, RaceData> raceDataMap = new HashMap<>();
        for (Race race : races)
            raceDataMap.put(race.getName(), new PwingRaceData(race.getName(), "data", playerConfig));

        if (!getRaceFromName(raceName).isPresent() && plugin.getConfigManager().isRequireRace()) {
            plugin.getLogger().severe("Could not find race " + raceName + ", please check the data config for " + player.getName() + "!");
            return false;
        }

        Race activeRace = getRaceFromName(raceName).get();
        racePlayers.put(player.getUniqueId(), new PwingRacePlayer(player, activeRace, raceDataMap));
        return true;
    }

    public void savePlayer(Player player) {
        RacePlayer racePlayer = getRacePlayer(player);
        if (racePlayer == null) {
            plugin.getLogger().severe("Could not save player " + player.getName() + "! An error may have occurred when this player first joined.");
            return;
        }

        RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getUniqueId());

        FileConfiguration config = playerConfig.getConfig();
        String raceName = racePlayer.getRace().isPresent() ? racePlayer.getRace().get().getName() : plugin.getConfigManager().getDefaultRace();
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

    public void registerPlayer(Player player, boolean override) {
        RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getUniqueId());

        FileConfiguration config = playerConfig.getConfig();
        if (!config.contains("active-race") || override) {
            boolean hasDefaultRace = plugin.getConfigManager().isDefaultRaceOnJoin();

            if (hasDefaultRace) {
                Optional<Race> defaultRace = getRaceFromName(plugin.getConfigManager().getDefaultRace());
                if (!defaultRace.isPresent()) {
                    plugin.getLogger().severe("Could not find default race " + plugin.getConfigManager().getDefaultRace() + "! Please make sure your config is correct!");
                } else {
                    config.set("active-race", plugin.getConfigManager().getDefaultRace());
                    defaultRace.get().getRaceItems().values().forEach(item -> ItemUtil.addItem(player, item));
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

    public boolean isRacesEnabledInWorld(World world) {
        return !plugin.getConfigManager().getDisabledWorlds().contains(world.getName());
    }

    public Map<UUID, RacePlayer> getRacePlayerMap() {
        return racePlayers;
    }
}