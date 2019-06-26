package net.pwing.races.race.trigger;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

public abstract class RaceTriggerPassive {

	protected PwingRaces plugin;
	protected String name;

	public RaceTriggerPassive(PwingRaces plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}

	public abstract void runTriggerPassive(Player player, String trigger);

	public void runPassive(Player player, String trigger) {
		String[] split = trigger.split(" ");

		if (!name.equalsIgnoreCase(split[0]))
			return;

		runTriggerPassive(player, trigger);
	}

	public String getName() {
		return name;
	}
}
