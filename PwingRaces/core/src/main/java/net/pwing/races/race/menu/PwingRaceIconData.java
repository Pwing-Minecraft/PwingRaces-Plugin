package net.pwing.races.race.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.menu.RaceIconData;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class PwingRaceIconData implements RaceIconData {

    private ItemStack unlockedIcon;
    private ItemStack lockedIcon;
    private ItemStack selectedIcon;

    private int iconSlot;

    public Optional<ItemStack> getLockedIcon() {
        return Optional.ofNullable(lockedIcon);
    }

    public Optional<ItemStack> getSelectedIcon() {
        return Optional.ofNullable(selectedIcon);
    }
}
