package net.pwing.races.race.trigger.passive;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.NumberUtil;

import org.bukkit.entity.Player;

public class GiveRaceExpTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public GiveRaceExpTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        int exp = 0;
        if (NumberUtil.isInteger(split[1]))
            exp = Integer.parseInt(split[1]);
        else if (NumberUtil.isRangedInteger(split[1]))
            exp = NumberUtil.getRangedInteger(split[1]);

        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        raceManager.getLevelManager().setExperience(player, race, raceManager.getPlayerData(player, race).getExperience() + exp);
    }
}
