package net.pwing.races.config;

import lombok.Getter;

import net.pwing.races.PwingRaces;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class RaceConfigurationManager {

	private PwingRaces plugin;

	private long autosave;
	private long attributeRefreshTime;
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
	private boolean giveItemsOnFirstSelect;
	private boolean allowReclaimingItems;
	private int reclaimItemsCost;
	private boolean allowReclaimingSkillpoints;
	private int reclaimSkillpointCost;
	private boolean reclaimSkillpointReduces;
	private double reclaimSkillpointReduction;
	private boolean sendSkillpointMessageOnJoin;
	private boolean useProjectileEvent;
	private boolean disableAbilitiesInCreative;
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
		attributeRefreshTime = fileConfiguration.getLong(configPath + ".attribute-refresh-time", 100);
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
		giveItemsOnFirstSelect = fileConfiguration.getBoolean(configPath + ".give-items-on-first-select", true);
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
		disableAbilitiesInCreative = fileConfiguration.getBoolean(configPath + ".disable-abilities-in-creative", true);
	}

	public void initConfigs() {
		try {
			Files.walk(Paths.get(plugin.getDataFolder().toString(), "races"))
					.filter(path -> path.getFileName().toString().endsWith(".yml"))
					.forEach(path -> {
						try {
							FileConfiguration config = YamlConfiguration.loadConfiguration(Files.newBufferedReader(path));
							raceConfigs.add(new RaceConfiguration(path, config));
						} catch (IOException ex) {
							plugin.getLogger().severe("Could not create messages.yml !!!");
							ex.printStackTrace();
						}
					});

			Path messagesPath = Paths.get(plugin.getDataFolder() + "/messages.yml");
			if (Files.notExists(messagesPath)) {
				Files.createFile(messagesPath);
				plugin.saveResource("messages.yml", true);
			}

			messageConfig = new RaceConfiguration(messagesPath, YamlConfiguration.loadConfiguration(Files.newBufferedReader(messagesPath)));
		} catch (IOException ex) {
			plugin.getLogger().severe("Could not create messages.yml !!!");
			ex.printStackTrace();
		}
	}

	public RaceConfiguration getPlayerDataConfig(UUID uuid) {
		try {
			Path playerDataPath = Paths.get(plugin.getDataFolder().toString(), "playerdata", uuid.toString() + ".yml");
			if (Files.notExists(playerDataPath)) {
				Files.createFile(playerDataPath);
			}
			return new RaceConfiguration(playerDataPath, YamlConfiguration.loadConfiguration(Files.newBufferedReader(playerDataPath)));
		} catch (IOException ex) {
			plugin.getLogger().severe("Could not create playerdata file for " + uuid.toString() + " !!!");
			ex.printStackTrace();
		}
		throw new RuntimeException("Failed to create data config for " + uuid.toString());
	}
}
