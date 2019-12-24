package net.pwing.races.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import lombok.Getter;

import net.pwing.races.PwingRaces;
import net.pwing.races.hooks.worldguard.IWorldGuardHandler;
import net.pwing.races.hooks.worldguard.WorldGuardHandlerDisabled;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldGuardHook extends PluginHook {

    private IWorldGuardHandler worldGuardHandler;

    @Getter
    private Map<UUID, List<String>> lastRegionsCache = new HashMap<>();

    public WorldGuardHook(PwingRaces owningPlugin, String pluginName) {
        super(owningPlugin, pluginName);
    }

    @Override
    public void enableHook(PwingRaces owningPlugin, Plugin hook) {
        worldGuardHandler = new WorldGuardHandlerDisabled();
        if (!(hook instanceof WorldGuardPlugin))
            return;

        owningPlugin.getLogger().info("WorldGuard found, region hook enabled.");

        Class<?> clazz = null;
        try {
            if (hook.getDescription().getVersion().startsWith("7")) {
                clazz = Class.forName("net.pwing.races.hooks.worldguard.WorldGuardHandler_v7");
            } else if (hook.getDescription().getVersion().startsWith("6")) {
                clazz = Class.forName("net.pwing.races.hooks.worldedit.WorldGuardHandler_v6");
            }
            worldGuardHandler = (IWorldGuardHandler) clazz.newInstance();
            owningPlugin.getLogger().info("Hooking into WorldGuard version " + hook.getDescription().getVersion());
        } catch (Exception ex) {
            owningPlugin.getLogger().warning("Could not properly hook into WorldGuard. Version " + hook.getDescription().getVersion() + " was detected, however PwingRaces requires WorldGuard v6 for 1.12 or WorldGuard v7 for 1.13+.");
            ex.printStackTrace();
        }
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
