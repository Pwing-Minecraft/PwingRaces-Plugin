package net.pwing.races.hooks.worldguard;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class WorldGuardHandler_v6 implements IWorldGuardHandler {

    @Override
    public boolean isInRegion(Location loc) {
        return WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc).getRegions().size() > 0;
    }

    @Override
    public boolean hasFlag(String flag, Location loc) {
        if (!isInRegion(loc))
            return false;

        RegionManager regionManager = WGBukkit.getRegionManager(loc.getWorld());
        Vector vec = BukkitUtil.toVector(loc);
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(vec);
        return regionSet.queryState(null, getFlagFromString(flag)) == StateFlag.State.ALLOW;
    }

    @Override
    public List<String> getRegions(Location loc) {
        return WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc).getRegions()
                .stream().map(ProtectedRegion::getId).collect(Collectors.toList());
    }

    private StateFlag getFlagFromString(String flagString) {
        for (Flag<?> flag : DefaultFlag.getFlags()) {
            if (flag.getName().equals(flagString) && flag instanceof StateFlag)
                return (StateFlag) flag;
        }

        throw new IllegalStateException("Worldguard flag " + flagString + " not found");
    }
}
