package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class GiveHealthTriggerPassive extends RaceTriggerPassive {

    public GiveHealthTriggerPassive(PwingRaces plugin, String name) {
        super(name);
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        double health = 0;
        if (NumberUtil.isDouble(trigger[1]))
            health = Double.parseDouble(trigger[1]);
        else if (NumberUtil.isRangedDouble(trigger[1]))
            health = NumberUtil.getRangedDouble(trigger[1]);

        if (player.getHealth() + health <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
            player.setHealth(player.getHealth() + health);
    }
}
