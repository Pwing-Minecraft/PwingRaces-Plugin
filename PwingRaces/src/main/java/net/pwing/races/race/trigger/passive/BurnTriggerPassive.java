package net.pwing.races.race.trigger.passive;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

public class BurnTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public BurnTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
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