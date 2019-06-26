package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;
import org.bukkit.entity.Player;

public class BurnTrigger extends RaceTriggerPassive {

    public BurnTrigger(PwingRaces plugin, String name) {
        super(plugin, name);
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");
        if (split.length < 2)
            return;

        int ticks = 60;
        if (NumberUtil.isInteger(split[1]))
            ticks = Integer.parseInt(split[1]);

        player.setFireTicks(ticks);
    }
}