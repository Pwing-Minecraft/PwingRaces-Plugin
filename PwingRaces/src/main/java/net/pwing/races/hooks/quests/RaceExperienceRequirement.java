package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.race.PwingRaceManager;
import net.pwing.races.race.PwingRacePlayer;

public class RaceExperienceRequirement extends CustomRequirement {

	private PwingRaceManager raceManager;

	public RaceExperienceRequirement(PwingRaceManager raceManager) {
		this.raceManager = raceManager;

		setName("Race Experience Requirement");
		setAuthor("Redned");
		addStringPrompt("Amount", "Enter the race experience requirement amount.", 0);
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		int amount = (int) data.get("Amount");

		PwingRacePlayer racePlayer = raceManager.getRacePlayer(player);
		if (racePlayer.getRaceData(racePlayer.getActiveRace()).getExperience() >= amount)
			return true;

		return false;
	}
}
