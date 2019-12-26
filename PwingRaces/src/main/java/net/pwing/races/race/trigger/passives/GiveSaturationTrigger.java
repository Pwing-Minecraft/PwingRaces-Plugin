package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.NumberUtil;

import org.bukkit.entity.Player;

public class GiveSaturationTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveSaturationTrigger(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        int saturation = 0;
        if (NumberUtil.isInteger(split[1]))
            saturation = Integer.parseInt(split[1]);
        else if (NumberUtil.isRangedInteger(split[1]))
            saturation = NumberUtil.getRangedInteger(split[1]);

        if (player.getFoodLevel() + saturation <= 20)
            player.setFoodLevel(player.getFoodLevel() + saturation);
    }
}
