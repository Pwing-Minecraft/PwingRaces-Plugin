package net.pwing.races.race.trigger.passive;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.NumberUtil;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class GiveHealthTriggerPassive extends RaceTriggerPassive {

    public GiveHealthTriggerPassive(PwingRaces plugin, String name) {
        super(name);
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        double health = 0;
        if (NumberUtil.isDouble(split[1]))
            health = Double.parseDouble(split[1]);
        else if (NumberUtil.isRangedDouble(split[1]))
            health = NumberUtil.getRangedDouble(split[1]);

        if (player.getHealth() + health <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
            player.setHealth(player.getHealth() + health);
    }
}
