package net.pwing.races.hook.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class WorldGuardHandler_v7 implements IWorldGuardHandler {

    @Override
    public boolean isInRegion(Location loc) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getApplicableRegions(BukkitAdapter.asBlockVector(loc)).getRegions().size() > 0;
    }

    @Override
    public boolean hasFlag(String flag, Location loc) {
        if (!isInRegion(loc))
            return false;

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager == null) {
            return false;
        }
        BlockVector3 vec = BukkitAdapter.asBlockVector(loc);
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(vec);
        return regionSet.queryState(null, getFlagFromString(flag)) == StateFlag.State.ALLOW;
    }

    @Override
    public List<String> getRegions(Location loc) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager == null) {
            return new ArrayList<>();
        }

        List<String> regions = new ArrayList<>();
        for (ProtectedRegion region : regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(loc)).getRegions()) {
            regions.add(region.getId());
        }

        return regions;
    }

    private StateFlag getFlagFromString(String flagString) {
        Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(flagString);
        if (flag instanceof StateFlag)
            return (StateFlag) flag;

        throw new IllegalStateException("Worldguard flag " + flagString + " not found");
    }
}
