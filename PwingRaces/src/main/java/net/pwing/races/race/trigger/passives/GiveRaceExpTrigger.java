package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.PwingRaceManager;
import org.bukkit.entity.Player;

import net.pwing.races.race.PwingRace;
import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

public class GiveRaceExpTrigger extends RaceTriggerPassive {

	public GiveRaceExpTrigger(PwingRaces plugin, String name) {
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

		PwingRaceManager raceManager = plugin.getRaceManager();
		PwingRace race = raceManager.getRacePlayer(player).getActiveRace();
		raceManager.getLevelManager().setExperience(player, race, raceManager.getPlayerData(player, race).getExperience() + exp);
	}
}
