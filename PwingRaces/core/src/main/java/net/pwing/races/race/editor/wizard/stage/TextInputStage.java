package net.pwing.races.race.editor.wizard.stage;

import net.pwing.races.race.editor.wizard.EditorContext;
import net.pwing.races.race.editor.wizard.WizardStage;
import net.pwing.races.util.InteractionInputs;

import java.util.function.Consumer;
import java.util.function.Function;

public class TextInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final String chatMessage;
    private final Function<String, Boolean> validContentFunction;
    private final Function<E, Consumer<String>> inputConsumer;

    public TextInputStage(String chatMessage, Function<E, Consumer<String>> inputConsumer) {
        this(chatMessage, null, inputConsumer);
    }

    public TextInputStage(String chatMessage, Function<String, Boolean> validContentFunction, Function<E, Consumer<String>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.validContentFunction = validContentFunction;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void apply(E context) {
        if (this.chatMessage != null) {
            context.getPlayer().sendMessage(this.chatMessage);
        }

        new InteractionInputs.ChatInput(context.getPlayer()) {

            @Override
            public void onChatInput(String input) {
                if ("cancel".equalsIgnoreCase(input)) {
                    context.getWizard().onCancel(context);
                    return;
                }

                inputConsumer.apply(context).accept(input);
                context.advanceStage();
            }

            @Override
            public boolean isValidChatInput(String input) {
                return !input.startsWith("/") && (validContentFunction == null || validContentFunction.apply(input));
            }
        };
    }
}
