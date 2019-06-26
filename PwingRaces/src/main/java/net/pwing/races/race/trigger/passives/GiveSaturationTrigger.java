package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

public class GiveSaturationTrigger extends RaceTriggerPassive {

	public GiveSaturationTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
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
