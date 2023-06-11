package net.pwing.races.race.editor.wizard;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

public abstract class EditorContext<E extends EditorContext<E>> {
    protected final RaceEditorWizard<E> wizard;
    protected final Player player;
    private Runnable advanceListener;

    public EditorContext(RaceEditorWizard<E> wizard, Player player) {
        this.wizard = wizard;
        this.player = player;
    }

    public PwingRaces getPlugin() {
        return this.wizard.getPlugin();
    }

    public RaceEditorWizard<E> getWizard() {
        return this.wizard;
    }

    public Player getPlayer() {
        return this.player;
    }

    void setAdvanceListener(Runnable listener) {
        this.advanceListener = listener;
    }

    public void advanceStage() {
        if (this.advanceListener != null) {
            this.advanceListener.run();
        }
    }

    public abstract boolean isComplete();
}
