package net.pwing.races.hooks.worldguard;

import org.bukkit.Location;
import org.bukkit.World;

public interface IWorldGuardHandler {

    boolean isInRegion(Location loc);
    boolean hasFlag(String flag, Location loc);
}
