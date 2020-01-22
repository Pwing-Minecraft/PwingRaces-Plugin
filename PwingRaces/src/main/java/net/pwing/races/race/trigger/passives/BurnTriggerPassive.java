package net.pwing.races.race.trigger.passives;

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
    public void runTriggerPassive(Player player, String[] trigger) {
        if (trigger.length < 2)
            return;

        int ticks = 60;
        if (NumberUtil.isInteger(trigger[1]))
            ticks = Integer.parseInt(trigger[1]);

        player.setFireTicks(ticks);
    }
}