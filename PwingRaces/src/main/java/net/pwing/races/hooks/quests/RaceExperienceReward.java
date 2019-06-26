package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;
import net.pwing.races.race.RaceManager;
import net.pwing.races.race.RacePlayer;
import net.pwing.races.race.RaceData;

public class RaceExperienceReward extends CustomReward {

	private RaceManager raceManager;

	public RaceExperienceReward(RaceManager raceManager) {
		this.raceManager = raceManager;

		setName("Race Experience Reward");
		setAuthor("Redned");
		setRewardName("Race Experience Reward");
		addStringPrompt("Amount", "Amount of race experience the player unlocks upon completing this quest.", 0);
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		int amount = (int) data.get("Amount");

		RacePlayer racePlayer = raceManager.getRacePlayer(player);
		RaceData raceData = racePlayer.getRaceData(racePlayer.getActiveRace());
		raceManager.getLevelManager().setExperience(player, racePlayer.getActiveRace(), raceData.getExperience() + amount);
	}
}
