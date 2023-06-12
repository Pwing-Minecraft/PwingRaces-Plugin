package net.pwing.races.race.editor.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.RaceEditorManager;
import net.pwing.races.race.editor.menu.button.EditorButton;
import net.pwing.races.race.editor.menu.edit.DisplayNameButton;
import net.pwing.races.race.editor.menu.edit.LockedIconButton;
import net.pwing.races.race.editor.menu.edit.NameButton;
import net.pwing.races.race.editor.menu.edit.RaceItemsButton;
import net.pwing.races.race.editor.menu.edit.SelectedIconButton;
import net.pwing.races.race.editor.menu.edit.UnlockedIconButton;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class RaceEditMenu {
    private final PwingRaces plugin;
    private final RaceEditorManager manager;
    private final Race race;

    public RaceEditMenu(PwingRaces plugin, RaceEditorManager manager, Race race) {
        this.plugin = plugin;
        this.manager = manager;
        this.race = race;
    }

    public void openMenu(Player player) {
        MenuBuilder builder = MenuBuilder.builder(this.plugin, "Race Editor", 45);

        // Column #3
        this.placeButton(builder, 5, UnlockedIconButton::new);
        this.placeButton(builder, 14, LockedIconButton::new);
        this.placeButton(builder, 23, SelectedIconButton::new);

        // Column #4
        this.placeButton(builder, 7, NameButton::new);
        this.placeButton(builder, 16, DisplayNameButton::new);
        this.placeButton(builder, 25, RaceItemsButton::new);

        builder.open(player);
    }

    public void placeButton(MenuBuilder builder, int slot, BiFunction<RaceEditMenu, Race, EditorButton> buttonFunction) {
        buttonFunction.apply(this, this.race).apply(slot, builder);
    }
}
