package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

public class GiveExpTrigger extends RaceTriggerPassive {

	public GiveExpTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
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
