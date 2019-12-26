package net.pwing.races.hook.worldguard;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class WorldGuardHandlerDisabled implements IWorldGuardHandler {

    @Override
    public boolean isInRegion(Location loc) {
        return false;
    }

    @Override
    public boolean hasFlag(String flag, Location loc) {
        return false;
    }

    @Override
    public List<String> getRegions(Location loc) {
        return new ArrayList<>();
    }
}
