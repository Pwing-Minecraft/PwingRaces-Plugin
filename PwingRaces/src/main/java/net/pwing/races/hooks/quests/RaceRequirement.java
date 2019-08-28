package net.pwing.races.hooks.quests;

import java.util.Map;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import org.bukkit.entity.Player;

public class RaceRequirement extends CustomRequirement {

    private RaceManager raceManager;

    public RaceRequirement(RaceManager raceManager) {
        this.raceManager = raceManager;

        setName("Race Requirement");
        setAuthor("Redned");
        addStringPrompt("Race", "Enter the race requirement.", null);
    }

    @Override
    public boolean testRequirement(Player player, Map<String, Object> data) {
        String raceStr = (String) data.get("Race");

        Race race = raceManager.getRaceFromName(raceStr);
        if (race == null)
            return false;

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer.getActiveRace().getName().equals(race))
            return true;

        return false;
    }
}
