package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

public class GiveHealthTrigger extends RaceTriggerPassive {

	public GiveHealthTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
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
