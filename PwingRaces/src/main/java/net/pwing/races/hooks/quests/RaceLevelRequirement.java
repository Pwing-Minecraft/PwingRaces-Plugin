package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.race.RaceManager;
import net.pwing.races.race.RacePlayer;

public class RaceLevelRequirement extends CustomRequirement {

	private RaceManager raceManager;

	public RaceLevelRequirement(RaceManager raceManager) {
		this.raceManager = raceManager;

		setName("Race Level Requirement");
		setAuthor("Redned");
		addStringPrompt("Amount", "Enter the race level requirement amount.", 0);
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		int amount = (int) data.get("Amount");

		RacePlayer racePlayer = raceManager.getRacePlayer(player);
		if (racePlayer.getRaceData(racePlayer.getActiveRace()).getLevel() >= amount)
			return true;

		return false;
	}
}
