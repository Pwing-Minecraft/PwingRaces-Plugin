package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

import org.bukkit.entity.Player;

public class GiveHealthTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveHealthTrigger(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        double health = 0;
        if (NumberUtil.isDouble(split[1]))
            health = Double.parseDouble(split[1]);
        else if (NumberUtil.isRangedDouble(split[1]))
            health = NumberUtil.getRangedDouble(split[1]);

        if (player.getHealth() + health <= plugin.getCompatCodeHandler().getMaxHealth(player))
            player.setHealth(player.getHealth() + health);
    }
}
