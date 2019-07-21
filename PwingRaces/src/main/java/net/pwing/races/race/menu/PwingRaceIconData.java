package net.pwing.races.race.menu;

import net.pwing.races.api.race.menu.RaceIconData;
import org.bukkit.inventory.ItemStack;

public class PwingRaceIconData implements RaceIconData {

    private ItemStack unlockedIcon;
    private ItemStack lockedIcon;
    private ItemStack selectedIcon;

    private int slot;

    public PwingRaceIconData(ItemStack unlockedIcon, ItemStack lockedIcon, ItemStack selectedIcon, int slot) {
        this.unlockedIcon = unlockedIcon;
        this.lockedIcon = lockedIcon;
        this.selectedIcon = selectedIcon;
        this.slot = slot;
    }

    public ItemStack getUnlockedIcon() {
        return unlockedIcon;
    }

    public void setUnlockedIcon(ItemStack unlockedIcon) {
        this.unlockedIcon = unlockedIcon;
    }

    public ItemStack getLockedIcon() {
        return lockedIcon;
    }

    public void setLockedIcon(ItemStack lockedIcon) {
        this.lockedIcon = lockedIcon;
    }

    public ItemStack getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(ItemStack selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getIconSlot() {
        return slot;
    }

    public void setIconSlot(int slot) {
        this.slot = slot;
    }
}
