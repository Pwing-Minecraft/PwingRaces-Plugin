package net.pwing.races.race.editor.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.race.editor.RaceEditorManager;
import net.pwing.races.race.editor.wizard.RaceEditorWizards;
import net.pwing.races.race.menu.MenuHandlers;
import net.pwing.races.util.InventoryUtil;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RaceSelectionMenu {
    private final PwingRaces plugin;
    private final RaceEditorManager manager;

    public RaceSelectionMenu(PwingRaces plugin, RaceEditorManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void openMenu(Player player) {
        RaceManager raceManager = this.plugin.getRaceManager();

        int slots = 18 + InventoryUtil.getRows(raceManager.getRaces().size()) * 9;
        MenuBuilder builder = MenuBuilder.builder(this.plugin, "Race Selection", slots);

        int i = 0;
        for (Race race : raceManager.getRaces()) {
            ItemBuilder icon = ItemBuilder.builder(race.getIconData().getUnlockedIcon());
            icon.name(race.getDisplayName());
            icon.lore("&7Click to edit");

            builder.item(icon, InventoryUtil.getPagedSlot(i++), MenuHandlers.simple(() -> new RaceEditMenu(plugin, manager, race).openMenu(player)));
        }

        builder.item(
                ItemBuilder.builder(Material.EMERALD)
                        .name("&aCreate Race")
                        .lore("&7Click to create a new race."),
                4,
                MenuHandlers.simple(RaceEditorWizards.RACE_CREATION::openWizard)
        );

        builder.open(player);
    }
}
