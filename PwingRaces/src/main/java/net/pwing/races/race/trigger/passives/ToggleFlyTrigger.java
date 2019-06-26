package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.Player;

public class ToggleFlyTrigger extends RaceTriggerPassive {

	public ToggleFlyTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void runTriggerPassive(Player player, String trigger) {
		String[] split = trigger.split(" ");
		if (split.length < 2)
			return;

		if (Boolean.parseBoolean(split[1]))
			player.setFlying(true);
		else
			player.setFlying(false);
	}
}
