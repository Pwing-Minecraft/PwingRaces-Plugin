package net.pwing.races.race.skilltree;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.utilities.ItemUtil;

public class RaceSkilltreeElement {

	private ItemStack iconUnlocked;
	private ItemStack iconPurchased;
	private ItemStack iconLocked;

	private String regName;
	private String title;
	private List<String> desc;

	private int slot;

	private int requiredParentAmount;
	private List<String> parents;
	private int cost;

	public RaceSkilltreeElement(String regName, String configPath, YamlConfiguration config) {
		this.regName = regName;

		loadDataFromConfig(configPath, config);
	}

	public void loadDataFromConfig(String configPath, YamlConfiguration config) {
		this.title = config.getString(configPath + ".title");
		this.desc = config.getStringList(configPath + ".description");
		this.requiredParentAmount = config.getInt(configPath + ".required-parent-amount", 1);
		this.parents = config.getStringList(configPath + ".parents");
		this.slot = config.getInt(configPath + ".slot", 0);
		this.cost = config.getInt(configPath + ".point-cost", 1);
		this.iconUnlocked = ItemUtil.readItemFromConfig(configPath + ".icon", config);
		this.iconLocked = ItemUtil.readItemFromConfig(configPath + ".icon-locked", config);
		this.iconPurchased = ItemUtil.readItemFromConfig(configPath + ".icon-purchased", config);

		if (config.contains(configPath + ".parent")) {
			this.parents.add(config.getString(configPath + ".parent"));
		}
	}

	public ItemStack getIcon() {
		return iconUnlocked;
	}

	public void setIcon(ItemStack iconUnlocked) {
		this.iconUnlocked = iconUnlocked;
	}

	public ItemStack getPurchasedIcon() {
		return iconPurchased;
	}

	public void setPurchasedIcon(ItemStack iconPurchased) {
		this.iconPurchased = iconPurchased;
	}

	public ItemStack getLockedIcon() {
		return iconLocked;
	}

	public void setLockedIcon(ItemStack iconLocked) {
		this.iconLocked = iconLocked;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getDescription() {
		return desc;
	}

	public void setDescrption(List<String> desc) {
		this.desc = desc;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getRequiredParentAmount() {
		return requiredParentAmount;
	}

	public void setRequiredParentAmount(int requiredParentAmount) {
		this.requiredParentAmount = requiredParentAmount;
	}

	public List<String> getParents() {
		return parents;
	}

	public void setParents(List<String> parents) {
		this.parents = parents;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

}
