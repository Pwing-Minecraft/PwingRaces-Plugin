package net.pwing.races.race.editor.wizard;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.PwingRace;
import net.pwing.races.race.editor.wizard.race.RaceCreateContext;
import net.pwing.races.race.editor.wizard.stage.ItemStackInputStage;
import net.pwing.races.race.editor.wizard.stage.TextInputStage;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class RaceEditorWizards {

    public static final RaceEditorWizard<RaceCreateContext> RACE_CREATION = createWizard(RaceCreateContext::new)
            .addStage(new TextInputStage<>(ChatColor.AQUA + "Type the name of the race in chat or \"cancel\" to cancel.", context -> context::setName))
            .addStage(new TextInputStage<>(ChatColor.AQUA + "Type the display name of the race in chat \"cancel\" to cancel.", context -> context::setDisplayName))
            .addStage(new ItemStackInputStage<>(ChatColor.AQUA + "Select the item in your inventory to use as the race icon", context -> context::setIconData))
            .addStage(new TextInputStage<>(
                    ChatColor.AQUA + "Type the menu slot of the race in chat",
                    NumberUtil::isInteger,
                    context -> content -> context.setIconSlot(NumberUtil.getInteger(content)))
            ).onComplete(context ->
                PwingRace.createFromContext(context).ifPresent(race -> {
                    context.getPlugin().getRaceManager().addRace(race);
                    context.getPlayer().sendMessage(ChatColor.GREEN + "Created race " + race.getName() + " successfully! You can now edit it in the editor.");
                })
            );

    public static <E extends EditorContext<E>> RaceEditorWizard<E> createWizard(BiFunction<RaceEditorWizard<E>, Player, E> contextFactory) {
        return new RaceEditorWizard<>(PwingRaces.getInstance(), contextFactory);
    }
}
