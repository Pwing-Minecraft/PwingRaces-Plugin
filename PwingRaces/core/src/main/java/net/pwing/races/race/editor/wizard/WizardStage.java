package net.pwing.races.race.editor.wizard;

public interface WizardStage<E extends EditorContext<E>> {

    void apply(E context);
}
