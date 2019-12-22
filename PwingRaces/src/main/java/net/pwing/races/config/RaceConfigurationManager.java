package net.pwing.races.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.pwing.races.PwingRaces;

@Getter
public class RaceConfigurationManager {

	private PwingRaces plugin;

	private long autosave;
	private boolean useTransientPermissions;
	private boolean requireRace;
	private boolean defaultRaceOnJoin;
	private String defaultRace;
	private boolean allowPlayerRaceChanges;
	private int raceChangeCost;
	private String raceChangeCostType;
	private boolean raceUnlocks;
	private boolean raceUnlockUsesCost;
	private boolean giveItemsOnRaceChange;
	private boolean allowReclaimingItems;
	private int reclaimItemsCost;
	private boolean allowReclaimingSkillpoints;
	private int reclaimSkillpointCost;
	private boolean reclaimSkillpointReduces;
	private double reclaimSkillpointReduction;
	private boolean sendSkillpointMessageOnJoin;
	private boolean useProjectileEvent;
	private List<String> projectileLaunchers;
	private List<String> projectileTypes;
	private List<String> disabledWorlds;

	private RaceConfiguration messageConfig;

	private List<RaceConfiguration> raceConfigs;

	public RaceConfigurationManager(PwingRaces plugin) {
		this.plugin = plugin;

		raceConfigs = new ArrayList<>();
		initConfigs();

		loadDataFromConfig("settings", plugin.getConfig());
	}

	public void loadDataFromConfig(String configPath, FileConfiguration fileConfiguration) {
		autosave = fileConfiguration.getLong(configPath + ".autosave", 6000);
		useTransientPermissions = fileConfiguration.getBoolean(configPath + ".use-transient-permissions", true);
		requireRace = fileConfiguration.getBoolean(configPath + ".require-race", true);
		defaultRace = fileConfiguration.getString(configPath + ".default-race");
		defaultRaceOnJoin = fileConfiguration.getBoolean(configPath + ".set-default-race-first-join", true);
		allowPlayerRaceChanges = fileConfiguration.getBoolean(configPath + ".allow-player-race-changes", false);
		raceChangeCost = fileConfiguration.getInt(configPath + ".race-change-cost", 25000);
		raceChangeCostType = fileConfiguration.getString(configPath + ".race-change-cost-type", "money");
		raceUnlocks = fileConfiguration.getBoolean(configPath + ".race-unlocks", true);
		raceUnlockUsesCost = fileConfiguration.getBoolean(configPath + ".race-unlock-uses-cost", false);
		giveItemsOnRaceChange = fileConfiguration.getBoolean(configPath + ".give-items-on-race-change", true);
		allowReclaimingItems = fileConfiguration.getBoolean(configPath + ".allow-reclaiming-items", true);
		reclaimItemsCost = fileConfiguration.getInt(configPath + ".reclaim-items-cost", 5000);
		allowReclaimingSkillpoints = fileConfiguration.getBoolean(configPath + ".allow-reclaiming-skillpoints", false);
		reclaimSkillpointCost = fileConfiguration.getInt(configPath + ".reclaim-skillpoint-cost", 15000);
		reclaimSkillpointReduces = fileConfiguration.getBoolean(configPath + ".reclaim-skillpoint-reduces", false);
		reclaimSkillpointReduction = fileConfiguration.getDouble(configPath + ".reclaim-skillpoint-reduction", 0.2);
		sendSkillpointMessageOnJoin = fileConfiguration.getBoolean(configPath + ".send-skillpoint-message-on-join", true);
		useProjectileEvent = fileConfiguration.getBoolean(configPath + ".use-projectile-event", true);
		projectileLaunchers = fileConfiguration.getStringList(configPath + ".projectile-launchers");
		projectileTypes = fileConfiguration.getStringList(configPath + ".projectile-types");
		disabledWorlds = fileConfiguration.getStringList(configPath + ".disabled-worlds");
	}

	public void initConfigs() {
		for (File file : new File(plugin.getDataFolder() + "/races/").listFiles()) {
			if (!file.getName().endsWith(".yml"))
				continue;

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			raceConfigs.add(new RaceConfiguration(file, config));
		}

		File messagesFile = new File(plugin.getDataFolder() + "/messages.yml");
		if (!messagesFile.exists()) {
			try {
				messagesFile.createNewFile();
				plugin.saveResource("messages.yml", true);
			} catch (IOException e) {
				plugin.getLogger().severe("Could not create messages.yml !!!");
			}
		}

		messageConfig = new RaceConfiguration(messagesFile, YamlConfiguration.loadConfiguration(messagesFile));
	}

	public RaceConfiguration getPlayerDataConfig(UUID uuid) {
		File file = new File(plugin.getDataFolder() + "/playerdata/" + uuid.toString() + ".yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().severe("Could not create playerdata file for " + uuid.toString() + " !!!");
				e.printStackTrace();
			}
		}

		return new RaceConfiguration(file, YamlConfiguration.loadConfiguration(file));
	}
}
