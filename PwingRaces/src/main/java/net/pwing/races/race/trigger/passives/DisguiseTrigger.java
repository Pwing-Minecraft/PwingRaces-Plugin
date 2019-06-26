package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pwing.races.race.trigger.RaceTriggerPassive;

public class DisguiseTrigger extends RaceTriggerPassive {

	public DisguiseTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void runTriggerPassive(Player player, String trigger) {
		String[] split = trigger.split(" ");
		if (split.length < 2)
			return;

		EntityType type = null;
		try {
			type = EntityType.valueOf(split[1].toUpperCase());
		} catch (IllegalArgumentException ex) {
			return;
		}

		plugin.getLibsDisguisesHook().disguiseEntity(player, type);
	}
}
