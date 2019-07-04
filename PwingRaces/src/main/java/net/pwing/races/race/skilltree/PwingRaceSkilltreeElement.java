package net.pwing.races.race.skilltree;

import java.util.List;

import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.utilities.ItemUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class PwingRaceSkilltreeElement implements RaceSkilltreeElement {

    private String internalName;
    private String title;
    private List<String> desc;

    private int slot;

    private ItemStack iconUnlocked;
    private ItemStack iconPurchased;
    private ItemStack iconLocked;

    private int requiredParentAmount;
    private List<String> parentElements;
    private int cost;

    public PwingRaceSkilltreeElement(String internalName, String configPath, FileConfiguration config) {
        this.internalName = internalName;

        loadDataFromConfig(configPath, config);
    }

    public void loadDataFromConfig(String configPath, FileConfiguration config) {
        this.title = config.getString(configPath + ".title");
        this.desc = config.getStringList(configPath + ".description");
        this.requiredParentAmount = config.getInt(configPath + ".required-parent-amount", 1);
        this.parentElements = config.getStringList(configPath + ".parents");
        this.slot = config.getInt(configPath + ".slot", 0);
        this.cost = config.getInt(configPath + ".point-cost", 1);
        this.iconUnlocked = ItemUtil.readItemFromConfig(configPath + ".icon", config);
        this.iconLocked = ItemUtil.readItemFromConfig(configPath + ".icon-locked", config);
        this.iconPurchased = ItemUtil.readItemFromConfig(configPath + ".icon-purchased", config);

        if (config.contains(configPath + ".parent")) {
            this.parentElements.add(config.getString(configPath + ".parent"));
        }
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
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

    public void setDescription(List<String> desc) {
        this.desc = desc;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
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

    public int getRequiredParentAmount() {
        return requiredParentAmount;
    }

    public void setRequiredParentAmount(int requiredParentAmount) {
        this.requiredParentAmount = requiredParentAmount;
    }

    public List<String> getParentElements() {
        return parentElements;
    }

    public void setParentElements(List<String> parentElements) {
        this.parentElements = parentElements;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}
