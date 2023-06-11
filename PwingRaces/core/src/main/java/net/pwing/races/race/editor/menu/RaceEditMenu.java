package net.pwing.races.race.editor.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.RaceEditorManager;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.entity.Player;

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
    }
}
