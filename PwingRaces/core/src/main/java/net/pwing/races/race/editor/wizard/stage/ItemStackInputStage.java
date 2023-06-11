package net.pwing.races.race.editor.wizard.stage;

import net.pwing.races.race.editor.wizard.EditorContext;
import net.pwing.races.race.editor.wizard.WizardStage;
import net.pwing.races.util.InteractionInputs;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final String chatMessage;
    private final Function<E, Consumer<ItemStack>> inputConsumer;

    public ItemStackInputStage(String chatMessage, Function<E, Consumer<ItemStack>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void apply(E context) {
        if (this.chatMessage != null) {
            context.getPlayer().sendMessage(this.chatMessage);
        }

        new InteractionInputs.InventoryInput(context.getPlayer()) {

            @Override
            public void onInventoryInteract(ItemStack item) {
                inputConsumer.apply(context).accept(item.clone());
                context.advanceStage();
            }
        };
    }
}
