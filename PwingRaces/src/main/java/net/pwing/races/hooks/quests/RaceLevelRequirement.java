package net.pwing.races.hooks.quests;

import java.util.Map;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import org.bukkit.entity.Player;

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
        Race race = racePlayer.getActiveRace();
        if (race == null)
            return false;

        if (racePlayer.getRaceData(race).getLevel() >= amount)
            return true;

        return false;
    }
}
