package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.entity.Player;

public class DamageTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public DamageTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        if (trigger.length < 2)
            return;

        double damage = 60;
        if (NumberUtil.isDouble(trigger[1]))
            damage = Double.parseDouble(trigger[1]);

        player.damage(damage);
    }
}
