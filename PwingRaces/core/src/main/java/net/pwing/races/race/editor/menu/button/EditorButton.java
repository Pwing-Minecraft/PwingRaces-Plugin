package net.pwing.races.race.editor.menu.button;

import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.menu.RaceEditMenu;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class EditorButton {
    protected final RaceEditMenu menu;
    protected final Race race;

    public EditorButton(RaceEditMenu menu, Race race) {
        this.menu = menu;
        this.race = race;
    }

    public abstract ItemStack getItem();

    public abstract void onClick(ClickType action);

    public final void apply(int slot, MenuBuilder builder) {
        builder.item(this.getItem(), slot, (player, action, item) -> onClick(action));
    }
}
