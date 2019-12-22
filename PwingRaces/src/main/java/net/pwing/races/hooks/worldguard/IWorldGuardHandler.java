package net.pwing.races.hooks.worldguard;

import org.bukkit.Location;

import java.util.List;

public interface IWorldGuardHandler {

    boolean isInRegion(Location loc);
    boolean hasFlag(String flag, Location loc);

    List<String> getRegions(Location loc);
}
