package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.NumberUtil;

import org.bukkit.entity.Player;

public class GiveExpTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveExpTrigger(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        int exp = 0;
        if (NumberUtil.isInteger(split[1]))
            exp = Integer.parseInt(split[1]);
        else if (NumberUtil.isRangedInteger(split[1]))
            exp = NumberUtil.getRangedInteger(split[1]);

        player.setTotalExperience(player.getTotalExperience() + exp);
    }
}
