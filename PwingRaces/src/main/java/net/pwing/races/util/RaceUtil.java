package net.pwing.races.util;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RacePlayer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RaceUtil {

    public static int getNearbyRaceCount(Location loc, Race race, double radius) {
        int nearby = -1; // since the player is included in the operation below
        for (Entity nearbyEntity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (!(nearbyEntity instanceof Player))
                continue;

            RacePlayer racePlayer = PwingRaces.getInstance().getRaceManager().getRacePlayer((Player) nearbyEntity);
            if (!racePlayer.getRace().isPresent())
                continue;

            if (racePlayer.getRace().get().equals(race))
                nearby += 1;
        }
        return Math.max(nearby, 0);
    }
}
