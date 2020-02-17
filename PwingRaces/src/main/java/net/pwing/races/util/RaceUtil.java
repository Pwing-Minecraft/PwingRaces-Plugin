package net.pwing.races.util;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RacePlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RaceUtil {

    public static CompletableFuture<Integer> getNearbyRaceCount(Location loc, Race race, double radius) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(PwingRaces.getInstance(), () -> {
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
            future.complete(Math.max(nearby, 0));
        });
        return future;
    }
}
