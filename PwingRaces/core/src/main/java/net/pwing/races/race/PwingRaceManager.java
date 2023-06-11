package net.pwing.races.race;

import lombok.Getter;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.PwingRacesAPI;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.config.RaceConfiguration;
import net.pwing.races.race.ability.PwingRaceAbilityManager;
import net.pwing.races.race.attribute.PwingRaceAttributeManager;
import net.pwing.races.race.editor.RaceEditorManager;
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

import java.nio.file.Paths;
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
    private final PwingRaces plugin;

    private final PwingRaceTriggerManager triggerManager;
    private final PwingRaceAttributeManager attributeManager;
    private final PwingRacePermissionManager permissionManager;
    private final PwingRaceLevelManager levelManager;
    private final PwingRaceAbilityManager abilityManager;
    private final PwingRaceSkilltreeManager skilltreeManager;
    private final RaceEditorManager editorManager;

    private RaceMenu raceMenu;

    private final Set<Race> races;
    private final Map<UUID, RacePlayer> racePlayers;

    public PwingRaceManager(PwingRaces plugin) {
        this.plugin = plugin;

        PwingRacesAPI.setRaceManager(this);

        Bukkit.getServer().getPluginManager().registerEvents(new RaceListener(plugin), plugin);

        races = new HashSet<>();
        racePlayers = new HashMap<>();

        triggerManager = new PwingRaceTriggerManager(plugin);
        attributeManager = new PwingRaceAttributeManager(plugin);
        permissionManager = new PwingRacePermissionManager(plugin);
        levelManager = new PwingRaceLevelManager(plugin);
        abilityManager = new PwingRaceAbilityManager(plugin);
        skilltreeManager = new PwingRaceSkilltreeManager(Paths.get(plugin.getDataFolder().toString(), "skilltrees"));
        editorManager = new RaceEditorManager(plugin);

        for (RaceConfiguration config : plugin.getConfigManager().getRaceConfigs())
            races.add(new PwingRace(this, config.getConfig()));

        FileConfiguration config = plugin.getConfig();
        raceMenu = new PwingRaceMenu(plugin, config.getString("menu.name", "Race Selection"), config.getInt("menu.slots", 45), config.getBoolean("menu.glass-filled", false));
    }

    public void addRace(Race race) {
        this.races.add(race);
        for (RacePlayer player : this.racePlayers.values()) {
            RaceConfiguration playerConfig = plugin.getConfigManager().getPlayerDataConfig(player.getPlayer().getUniqueId());
            if (playerConfig == null) {
                plugin.getLogger().severe("Could not access player data file for " + player.getPlayer().getName() + "!");
                continue;
            }

            player.getRaceDataMap().put(race.getName(), new PwingRaceData(race.getName(), "data", playerConfig));
        }
    }

    public void reloadRaces() {
        races.clear();
        racePlayers.clear();

        skilltreeManager.getSkilltrees().clear();
        skilltreeManager.initSkilltrees(Paths.get(plugin.getDataFolder().toString(), "skilltrees"));

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

        if (getRaceFromName(raceName).isEmpty() && plugin.getConfigManager().isRequireRace()) {
            plugin.getLogger().severe("Could not find race " + raceName + ", please check the data config for " + player.getName() + "!");
            return false;
        }

        Race activeRace = getRaceFromName(raceName).orElse(null);
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
            config.set("data." + race.getName() + ".played", data.hasPlayed());

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
                if (defaultRace.isEmpty()) {
                    plugin.getLogger().severe("Could not find default race " + plugin.getConfigManager().getDefaultRace() + "! Please make sure your config is correct!");
                    config.set("active-race", "");
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
                config.set(racePath + ".played", false);
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