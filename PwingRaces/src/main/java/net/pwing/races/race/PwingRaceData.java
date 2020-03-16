package net.pwing.races.race;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.RaceData;
import net.pwing.races.config.RaceConfiguration;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PwingRaceData implements RaceData {

    private String raceName;

	private boolean unlocked;

	private boolean played;

	private int level;
	private int experience;

	private int usedSkillpoints;
	private int unusedSkillpoints;

	private Map<String, List<String>> purchasedElementsMap;
	private RaceConfiguration playerConfig;

	public PwingRaceData(String raceName, String configPath, RaceConfiguration playerConfig) {
		this.raceName = raceName;
		this.playerConfig = playerConfig;

		loadDataFromConfig(configPath);
	}

	public void loadDataFromConfig(String configPath) {
		FileConfiguration config = playerConfig.getConfig();

		this.unlocked = config.getBoolean(configPath + "." + raceName + ".unlocked");
		this.played = config.getBoolean(configPath + "." + raceName + ".played", true);
		this.level = config.getInt(configPath + "." + raceName + ".level");
		this.experience = config.getInt(configPath + "." + raceName + ".exp");
		this.usedSkillpoints = config.getInt(configPath + "." + raceName + ".used-skillpoints");
		this.unusedSkillpoints = config.getInt(configPath + "." + raceName + ".unused-skillpoints");
		this.purchasedElementsMap = new HashMap<>();

		if (config.contains(configPath + "." + raceName + ".purchased-elements")) {
			for (String str : config.getConfigurationSection(configPath + "." + raceName + ".purchased-elements").getKeys(false)) {
				List<String> elements = config.getStringList(configPath + "." + raceName + ".purchased-elements." + str);
				purchasedElementsMap.put(str, elements);
			}
		}
	}

	@Override
	public boolean hasPlayed() {
		return played;
	}

	@Override
	public void setHasPlayed(boolean played) {
		this.played = played;
	}

	public boolean hasPurchasedElement(String skillTree, String name) {
		if (name.equals(raceName.toLowerCase()))
			return true;

		return getPurchasedElements(skillTree).contains(name);
	}

	public void addPurchasedElement(String skillTree, String name) {
		List<String> purchased = purchasedElementsMap.get(skillTree);
		if (!purchased.contains(name))
			purchased.add(name);

		purchasedElementsMap.put(skillTree, purchased);
	}

	public void removePurchasedElement(String skillTree, String name) {
		List<String> purchased = getPurchasedElements(skillTree);
		purchased.remove(name);

		purchasedElementsMap.put(skillTree, purchased);
	}

	public List<String> getPurchasedElements(String skillTree) {
		return !purchasedElementsMap.containsKey(skillTree) ? new ArrayList<>() : purchasedElementsMap.get(skillTree);
	}
}
