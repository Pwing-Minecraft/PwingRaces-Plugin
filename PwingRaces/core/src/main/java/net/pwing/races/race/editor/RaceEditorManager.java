package net.pwing.races.race.editor;

import net.pwing.races.PwingRaces;

public class RaceEditorManager {
    private final PwingRaces plugin;
    private final RaceEditorMainMenu menu;

    public RaceEditorManager(PwingRaces plugin) {
        this.plugin = plugin;
        this.menu = new RaceEditorMainMenu(plugin, this);
    }

    public RaceEditorMainMenu getRaceEditorMenu() {
        return this.menu;
    }
}
