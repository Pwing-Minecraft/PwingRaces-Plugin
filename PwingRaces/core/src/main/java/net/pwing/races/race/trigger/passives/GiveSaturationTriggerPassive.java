package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

public class GiveSaturationTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveSaturationTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        int saturation = 0;
        if (NumberUtil.isInteger(trigger[1]))
            saturation = Integer.parseInt(trigger[1]);
        else if (NumberUtil.isRangedInteger(trigger[1]))
            saturation = NumberUtil.getRangedInteger(trigger[1]);

        if (player.getFoodLevel() + saturation <= 20)
            player.setFoodLevel(player.getFoodLevel() + saturation);
    }
}
