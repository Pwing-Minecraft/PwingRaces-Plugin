package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

public class GiveExpTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveExpTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        int exp = 0;
        if (NumberUtil.isInteger(trigger[1]))
            exp = Integer.parseInt(trigger[1]);
        else if (NumberUtil.isRangedInteger(trigger[1]))
            exp = NumberUtil.getRangedInteger(trigger[1]);

        player.setTotalExperience(player.getTotalExperience() + exp);
    }
}
