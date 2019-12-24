package net.pwing.races.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

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
        BlockVector3 vec = BukkitAdapter.asBlockVector(loc);
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(vec);
        return regionSet.queryState(null, getFlagFromString(flag)) == StateFlag.State.ALLOW;
    }

    @Override
    public List<String> getRegions(Location loc) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()))
                .getApplicableRegions(BukkitAdapter.asBlockVector(loc)).getRegions().stream().map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }

    private StateFlag getFlagFromString(String flagString) {
        Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(flagString);
        if (flag instanceof StateFlag)
            return (StateFlag) flag;

        throw new IllegalStateException("Worldguard flag " + flagString + " not found");
    }
}
