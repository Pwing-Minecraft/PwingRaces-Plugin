package net.pwing.races.hooks.quests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;
import net.pwing.races.race.Race;
import net.pwing.races.race.RaceManager;
import net.pwing.races.race.RacePlayer;

public class RaceReward extends CustomReward {

	private RaceManager raceManager;

	public RaceReward(RaceManager raceManager) {
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

		Race race = raceManager.getRaceFromName(raceStr);
		if (race == null)
			return;

		RacePlayer racePlayer = raceManager.getRacePlayer(player);
		racePlayer.getRaceData(race).setUnlocked(true);
	}
}
