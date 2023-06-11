package net.pwing.races.race.editor;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.editor.menu.RaceSelectionMenu;
import net.pwing.races.race.editor.menu.RaceSettingsMenu;
import net.pwing.races.race.menu.MenuHandlers;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RaceEditorMainMenu {
    private final PwingRaces plugin;

    private final RaceSelectionMenu raceSelectionMenu;
    private final RaceSettingsMenu raceSettingsMenu;

    public RaceEditorMainMenu(PwingRaces plugin, RaceEditorManager manager) {
        this.plugin = plugin;

        this.raceSelectionMenu = new RaceSelectionMenu(plugin, manager);
        this.raceSettingsMenu = new RaceSettingsMenu(plugin, manager);
    }

    public void openMenu(Player player) {
        MenuBuilder builder = MenuBuilder.builder(this.plugin, "Race Editor", 27);

        // Race editor
        builder.item(
                ItemBuilder.builder(Material.ALLAY_SPAWN_EGG)
                        .name("&bRace Editor")
                        .lore("&7Click to modify or create new races."),
                11,
                MenuHandlers.simple(this.raceSelectionMenu::openMenu)
        );

        // Settings
        builder.item(
                ItemBuilder.builder(Material.COMMAND_BLOCK)
                        .name("&eSettings")
                        .lore("&7Click to modify plugin settings."),
                15,
                MenuHandlers.simple(this.raceSettingsMenu::openMenu)
        );

        builder.open(player);
    }
}
