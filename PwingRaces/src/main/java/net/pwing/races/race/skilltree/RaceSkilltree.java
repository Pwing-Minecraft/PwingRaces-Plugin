package net.pwing.races.race.skilltree;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.utilities.ItemUtil;

public class RaceSkilltree {

	private String regName;
	private String name;

	private ItemStack icon;
	private ItemStack menuIcon;
	private int menuSlot;
	private int menuSize;

	private List<RaceSkilltreeElement> elements;

	public RaceSkilltree(String key, YamlConfiguration config) {
		this.regName = key;

		loadDataFromConfig(key, config);
	}

	public void loadDataFromConfig(String key, YamlConfiguration config) {
		this.name = config.getString("name");
		this.icon = ItemUtil.readItemFromConfig("icon", config);
		if (icon == null)
			icon = new ItemStack(Material.STONE);

		this.menuIcon = ItemUtil.readItemFromConfig("menu-icon", config);
		if (menuIcon == null)
			menuIcon = icon;

		this.menuSlot = config.getInt("menu-slot", -1);
		this.menuSize = config.getInt("menu-size", 54);

		List<RaceSkilltreeElement> elements = new ArrayList<RaceSkilltreeElement>();
		if (config.contains("elements")) {
			for (String elem : config.getConfigurationSection("elements").getKeys(false))
				elements.add(new RaceSkilltreeElement(elem, "elements." + elem, config));
		}

		this.elements = elements;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public ItemStack getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(ItemStack menuIcon) {
		this.menuIcon = menuIcon;
	}

	public int getMenuSlot() {
		return menuSlot;
	}

	public void setMenuSlot(int menuSlot) {
		this.menuSlot = menuSlot;
	}

	public int getMenuSize() {
		return menuSize;
	}

	public void setMenuSize(int menuSize) {
		this.menuSize = menuSize;
	}

	public List<RaceSkilltreeElement> getElements() {
		return elements;
	}

	public void setElements(List<RaceSkilltreeElement> elements) {
		this.elements = elements;
	}

	public RaceSkilltreeElement getElementFromName(String name) {
		for (RaceSkilltreeElement element : elements) {
			if (element.getRegName().equals(name))
				return element;

			if (element.getTitle() != null && element.getTitle().isEmpty()) {
				if (element.getTitle().equals(name))
					return element;
			}
		}

		return null;
	}
}
