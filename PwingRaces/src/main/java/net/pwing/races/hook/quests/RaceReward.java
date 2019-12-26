package net.pwing.races.hook.quests;

import java.util.Map;

import me.blackvein.quests.CustomReward;
import net.pwing.races.api.events.RaceUnlockEvent;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        if (!raceManager.getRaceFromName(raceStr).isPresent())
            return;

        Race race = raceManager.getRaceFromName(raceStr).get();
        RaceUnlockEvent event = new RaceUnlockEvent(player, race);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            MessageUtil.sendMessage(player, "cannot-unlock-race", "%prefix% &cCannot unlock race.");
            return;
        }

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        racePlayer.getRaceData(event.getRace()).setUnlocked(true);
    }
}
