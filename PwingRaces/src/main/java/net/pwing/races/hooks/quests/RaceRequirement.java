package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.race.RaceManager;
import net.pwing.races.race.RacePlayer;

public class RaceRequirement extends CustomRequirement {

	private RaceManager raceManager;

	public RaceRequirement(RaceManager raceManager) {
		this.raceManager = raceManager;

		setName("Race Level Requirement");
		setAuthor("Redned");
		addStringPrompt("Race", "Enter the race requirement.", null);
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		String race = (String) data.get("Race");

		RacePlayer racePlayer = raceManager.getRacePlayer(player);
		if (racePlayer.getActiveRace().getName().equals(race))
			return true;

		return false;
	}
}
