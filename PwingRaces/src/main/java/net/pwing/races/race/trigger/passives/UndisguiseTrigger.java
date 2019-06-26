package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.race.trigger.RaceTriggerManager;

public class UndisguiseTrigger extends RaceTriggerPassive {

	public UndisguiseTrigger(PwingRaces plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void runTriggerPassive(Player player, String trigger) {
		plugin.getLibsDisguisesHook().undisguiseEntity(player);
	}
}
