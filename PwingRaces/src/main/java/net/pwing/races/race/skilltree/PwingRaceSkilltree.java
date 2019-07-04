package net.pwing.races.race.skilltree;

import java.util.ArrayList;
import java.util.List;

import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.utilities.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class PwingRaceSkilltree implements RaceSkilltree {

    private String internalName;
    private String name;

    private ItemStack icon;
    private ItemStack menuIcon;
    private int menuSlot;
    private int menuSize;

    private List<RaceSkilltreeElement> elements;

    public PwingRaceSkilltree(String key, FileConfiguration config) {
        this.internalName = key;

        loadDataFromConfig(config);
    }

    public void loadDataFromConfig(FileConfiguration config) {
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
                elements.add(new PwingRaceSkilltreeElement(elem, "elements." + elem, config));
        }

        this.elements = elements;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
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
            if (element.getInternalName().equals(name))
                return element;

            if (element.getTitle() != null && element.getTitle().isEmpty()) {
                if (element.getTitle().equals(name))
                    return element;
            }
        }

        return null;
    }
}
