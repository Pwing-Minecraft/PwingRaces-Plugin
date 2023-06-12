package net.pwing.races.race.editor.menu.edit;

import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.menu.RaceEditMenu;
import net.pwing.races.race.editor.menu.RaceItemsMenu;
import net.pwing.races.race.editor.menu.button.EditorButton;
import net.pwing.races.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RaceItemsButton extends EditorButton {

    public RaceItemsButton(RaceEditMenu menu, Race race) {
        super(menu, race);
    }

    @Override
    public ItemStack getItem() {
        return ItemBuilder.builder(Material.CHEST)
                .name("&aRace Items")
                .lore(List.of("&7Click to configure race items."))
                .build();
    }

    @Override
    public void onClick(Player player, ClickType action) {
        new RaceItemsMenu(this.race).openMenu(player);
    }
}
