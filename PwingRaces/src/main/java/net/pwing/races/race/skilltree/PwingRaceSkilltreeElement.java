package net.pwing.races.race.skilltree;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.utilities.ItemUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class PwingRaceSkilltreeElement implements RaceSkilltreeElement {

    private String internalName;
    private String title;
    private List<String> description;

    private int slot;

    private ItemStack icon;
    private ItemStack purchasedIcon;
    private ItemStack lockedIcon;

    private int requiredParentAmount;
    private List<String> parentElements;
    private int cost;

    public PwingRaceSkilltreeElement(String internalName, String configPath, FileConfiguration config) {
        this.internalName = internalName;

        loadDataFromConfig(configPath, config);
    }

    public void loadDataFromConfig(String configPath, FileConfiguration config) {
        this.title = config.getString(configPath + ".title");
        this.description = config.getStringList(configPath + ".description");
        this.requiredParentAmount = config.getInt(configPath + ".required-parent-amount", 1);
        this.parentElements = config.getStringList(configPath + ".parents");
        this.slot = config.getInt(configPath + ".slot", 0);
        this.cost = config.getInt(configPath + ".point-cost", 1);
        this.icon = ItemUtil.readItemFromConfig(configPath + ".icon", config);
        this.lockedIcon = ItemUtil.readItemFromConfig(configPath + ".icon-locked", config);
        this.purchasedIcon = ItemUtil.readItemFromConfig(configPath + ".icon-purchased", config);

        if (config.contains(configPath + ".parent")) {
            this.parentElements.add(config.getString(configPath + ".parent"));
        }
    }

    public Optional<ItemStack> getPurchasedIcon() {
        return Optional.ofNullable(purchasedIcon);
    }

    public Optional<ItemStack> getLockedIcon() {
        return Optional.ofNullable(lockedIcon);
    }
}
