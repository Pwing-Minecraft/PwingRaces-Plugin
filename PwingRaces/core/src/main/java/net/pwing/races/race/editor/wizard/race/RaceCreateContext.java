package net.pwing.races.race.editor.wizard.race;

import lombok.Getter;
import lombok.Setter;
import net.pwing.races.race.editor.wizard.EditorContext;
import net.pwing.races.race.editor.wizard.RaceEditorWizard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class RaceCreateContext extends EditorContext<RaceCreateContext> {
    private String name;
    private String displayName;
    private ItemStack iconData;
    private int iconSlot;

    public RaceCreateContext(RaceEditorWizard<RaceCreateContext> wizard, Player player) {
        super(wizard, player);
    }

    @Override
    public boolean isComplete() {
        return this.name != null
                && this.displayName != null
                && this.iconData != null;
    }
}
