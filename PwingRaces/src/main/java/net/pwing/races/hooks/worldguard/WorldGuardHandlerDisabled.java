package net.pwing.races.hooks.worldguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldGuardHandlerDisabled implements IWorldGuardHandler {

    @Override
    public boolean isInRegion(Location loc) {
        return false;
    }

    @Override
    public boolean hasFlag(String flag, Location loc) {
        return false;
    }
}
