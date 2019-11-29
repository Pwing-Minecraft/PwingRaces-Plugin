package net.pwing.races.hooks.quests;

import java.util.Map;

import me.blackvein.quests.CustomReward;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import org.bukkit.entity.Player;

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
		if (!racePlayer.getRace().isPresent())
			return;

		Race race = racePlayer.getRace().get();

		RaceData raceData = racePlayer.getRaceData(race);
		raceManager.getLevelManager().setExperience(player, race, raceData.getExperience() + amount);
	}
}
