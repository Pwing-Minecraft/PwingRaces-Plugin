package net.pwing.races.race.editor.wizard;

import org.bukkit.entity.Player;

public class BaseEditorContext extends EditorContext<BaseEditorContext> {
    public BaseEditorContext(RaceEditorWizard<BaseEditorContext> wizard, Player player) {
        super(wizard, player);
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
