package net.pwing.races.race;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pwing.races.api.race.RaceData;
import org.bukkit.configuration.file.YamlConfiguration;

import net.pwing.races.config.RaceConfiguration;

public class PwingRaceData implements RaceData {

	private String raceName;

	private boolean unlocked;

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
		YamlConfiguration config = playerConfig.getConfig();

		this.unlocked = config.getBoolean(configPath + "." + raceName + ".unlocked");
		this.level = config.getInt(configPath + "." + raceName + ".level");
		this.experience = config.getInt(configPath + "." + raceName + ".exp");
		this.usedSkillpoints = config.getInt(configPath + "." + raceName + ".used-skillpoints");
		this.unusedSkillpoints = config.getInt(configPath + "." + raceName + ".unused-skillpoints");
		this.purchasedElementsMap = new HashMap<String, List<String>>();

		if (config.contains(configPath + "." + raceName + ".purchased-elements")) {
			for (String str : config.getConfigurationSection(configPath + "." + raceName + ".purchased-elements").getKeys(false)) {
				List<String> elements = config.getStringList(configPath + "." + raceName + ".purchased-elements." + str);
				purchasedElementsMap.put(str, elements);
			}
		}
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getUnusedSkillpoints() {
		return unusedSkillpoints;
	}

	public void setUnusedSkillpoints(int unusedSkillpoints) {
		this.unusedSkillpoints = unusedSkillpoints;
	}

	public int getUsedSkillpoints() {
		return usedSkillpoints;
	}

	public void setUsedSkillpoints(int usedSkillpoints) {
		this.usedSkillpoints = usedSkillpoints;
	}

	public boolean hasPurchasedElement(String skillTree, String name) {
		if (name.equals(raceName.toLowerCase()))
			return true;

		if (getPurchasedElements(skillTree) == null)
			return false;

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
		if (purchased.contains(name))
			purchased.remove(name);

		purchasedElementsMap.put(skillTree, purchased);
	}

	public List<String> getPurchasedElements(String skillTree) {
		return purchasedElementsMap.get(skillTree);
	}

	public Map<String, List<String>> getPurchasedElementsMap() {
		return purchasedElementsMap;
	}
}
