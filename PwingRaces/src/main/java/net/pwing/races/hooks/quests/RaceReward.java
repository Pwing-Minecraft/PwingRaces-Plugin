package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;
import net.pwing.races.race.PwingRace;
import net.pwing.races.race.PwingRaceManager;
import net.pwing.races.race.PwingRacePlayer;

public class RaceReward extends CustomReward {

	private PwingRaceManager raceManager;

	public RaceReward(PwingRaceManager raceManager) {
		this.raceManager = raceManager;

		setName("Race Reward");
		setAuthor("Redned");
		setRewardName("Race Reward");
		addStringPrompt("Race to Unlock", "Enter the race the player unlocks upon completing this quest.", null);
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		String raceStr = (String) data.get("Race Reward");
		if (raceStr == null)
			return;

		PwingRace race = raceManager.getRaceFromName(raceStr);
		if (race == null)
			return;

		PwingRacePlayer racePlayer = raceManager.getRacePlayer(player);
		racePlayer.getRaceData(race).setUnlocked(true);
	}
}
