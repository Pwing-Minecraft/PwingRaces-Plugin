package net.pwing.races.race.trigger.passive;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.MessageUtil;

import org.bukkit.entity.Player;

public class SendMessageTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public SendMessageTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < split.length; i++) {
            builder.append(split[i]).append(" ");
        }

        String message = builder.toString();
        player.sendMessage(MessageUtil.getPlaceholderMessage(player, message));
    }
}
