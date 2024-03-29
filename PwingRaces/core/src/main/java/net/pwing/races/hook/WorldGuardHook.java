package net.pwing.races.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.pwing.races.PwingRaces;
import net.pwing.races.hook.worldguard.WorldGuardHandler;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldGuardHook extends PluginHook {

    private WorldGuardHandler worldGuardHandler;

    @Getter
    private final Map<UUID, List<String>> lastRegionsCache = new HashMap<>();

    public WorldGuardHook(PwingRaces owningPlugin, String pluginName) {
        super(owningPlugin, pluginName);
    }

    @Override
    public void enableHook(PwingRaces owningPlugin, Plugin hook) {
        if (!(hook instanceof WorldGuardPlugin)) {
            return;
        }

        owningPlugin.getLogger().info("WorldGuard found, region hook enabled.");

        worldGuardHandler = new WorldGuardHandler();
    }

    public boolean isInRegion(Location loc) {
        return worldGuardHandler.isInRegion(loc);
    }

    public List<String> getRegions(Location loc) {
        return worldGuardHandler.getRegions(loc);
    }

    public boolean hasFlag(String flag, Location loc) {
        return worldGuardHandler.hasFlag(flag, loc);
    }
}
