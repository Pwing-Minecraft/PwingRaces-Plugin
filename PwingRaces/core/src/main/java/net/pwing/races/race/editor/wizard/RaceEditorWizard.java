package net.pwing.races.race.editor.wizard;

import net.pwing.races.PwingRaces;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RaceEditorWizard<E extends EditorContext<E>> {
    private final PwingRaces plugin;
    private final BiFunction<RaceEditorWizard<E>, Player, E> contextFactory;

    private final List<WizardStage<E>> stages = new ArrayList<>();
    private Consumer<E> onComplete;
    private Consumer<E> onCancel;

    public RaceEditorWizard(PwingRaces plugin, BiFunction<RaceEditorWizard<E>, Player, E> contextFactory) {
        this.plugin = plugin;
        this.contextFactory = contextFactory;
    }

    PwingRaces getPlugin() {
        return this.plugin;
    }

    public RaceEditorWizard<E> addStage(WizardStage<E> stage) {
        this.stages.add(stage);
        return this;
    }

    public RaceEditorWizard<E> onComplete(Consumer<E> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public RaceEditorWizard<E> onCancel(Consumer<E> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public void onCancel(E context) {
        if (this.onCancel != null) {
            this.onCancel.accept(context);
        }
    }

    public void openWizard(Player player) {
        if (this.stages.isEmpty()) {
            this.plugin.getLogger().warning("No stages have been added to wizard" + this.getClass().getSimpleName() + "!");
            return;
        }

        // Close any open inventories
        player.closeInventory();

        E context = this.contextFactory.apply(this, player);
        AtomicInteger cursor = new AtomicInteger();
        context.setAdvanceListener(() -> {
            if (cursor.get() >= this.stages.size()) {
                if (!context.isComplete()) {
                    player.sendMessage(ChatColor.RED + "An error occurred while applying changes. Please see the console for more information!");
                }

                if (this.onComplete != null) {
                    this.onComplete.accept(context);
                }
            } else {
                WizardStage<E> stage = this.stages.get(cursor.getAndIncrement());
                stage.apply(context);
            }
        });

        WizardStage<E> initialStage = this.stages.get(cursor.getAndIncrement());
        initialStage.apply(context);
    }
}
