package net.pwing.races.race.trigger.conditions;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.RaceUtil;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class NearRaceCondition implements RaceCondition {

    private RaceManager raceManager;

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 4)
            return false;

        Optional<Race> race = raceManager.getRaceFromName(args[0]);
        if (!race.isPresent())
            return false;

        double radius = NumberUtil.getDouble(args[1]);
        int requiredNearby = NumberUtil.getInteger(args[2]);
        return RaceUtil.getNearbyRaceCount(player.getLocation(), race.get(), radius) >= requiredNearby;
    }
}
