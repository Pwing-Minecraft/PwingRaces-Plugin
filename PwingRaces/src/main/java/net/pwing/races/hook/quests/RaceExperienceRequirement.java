package net.pwing.races.hook.quests;

import java.util.Map;

import me.blackvein.quests.CustomRequirement;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import org.bukkit.entity.Player;

public class RaceExperienceRequirement extends CustomRequirement {

    private RaceManager raceManager;

    public RaceExperienceRequirement(RaceManager raceManager) {
        this.raceManager = raceManager;

        setName("Race Experience Requirement");
        setAuthor("Redned");
        addStringPrompt("Amount", "Enter the race experience requirement amount.", 0);
    }

    @Override
    public boolean testRequirement(Player player, Map<String, Object> data) {
        int amount = (int) data.get("Amount");

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        return racePlayer.getRace().map(race -> racePlayer.getRaceData(race).getExperience() >= amount).orElse(false);
    }
}
