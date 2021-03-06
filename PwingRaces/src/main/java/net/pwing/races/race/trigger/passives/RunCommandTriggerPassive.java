package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RunCommandTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public RunCommandTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < trigger.length; i++) {
            builder.append(trigger[i]).append(" ");
        }

        String command = builder.toString();
        if (command.startsWith("console: ")) {
            command = command.substring(9);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MessageUtil.getPlaceholderMessage(player, command));
            return;
        }

        if (command.startsWith("op: ")) {
            command = command.substring(4);

            player.setOp(true);
            player.chat("/" + MessageUtil.getPlaceholderMessage(player, command));
            player.setOp(false);
            return;
        }

        // Use Player#chat so BungeeCord commands are able to be ran through here
        player.chat("/" + MessageUtil.getPlaceholderMessage(player, command));
    }
}
