package net.pwing.races.hooks;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.pwing.races.hooks.worldedit.IWorldEditHandler;
import net.pwing.races.hooks.worldedit.WorldEditHandlerDisabled;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import net.pwing.races.PwingRaces;

public class WorldEditHook extends PluginHook {

	private IWorldEditHandler worldEditHandler;

	public WorldEditHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		worldEditHandler = new WorldEditHandlerDisabled();
		if (!(hook instanceof WorldEditPlugin))
			return;

		owningPlugin.getLogger().info("WorldEdit found, schematic hook enabled.");

		Class<?> clazz = null;
		try {
			if (hook.getDescription().getVersion().startsWith("7")) {
				clazz = Class.forName("net.pwing.races.hooks.worldedit.WorldEditHandler_v7");
			} else if (hook.getDescription().getVersion().startsWith("6")) {
				clazz = Class.forName("net.pwing.races.hooks.worldedit.WorldEditHandler_v6");
			}
			worldEditHandler = (IWorldEditHandler) clazz.newInstance();
			owningPlugin.getLogger().info("Hooking into WorldEdit version " + hook.getDescription().getVersion());
		} catch (Exception ex) {
			owningPlugin.getLogger().warning("Could not properly hook into WorldEdit. Version " + hook.getDescription().getVersion() + " was detected, however PwingRaces requires WorldEdit v6 for 1.8 - 1.12 or WorldEdit v7 for 1.13+.");
			ex.printStackTrace();
		}
	}

	public void pasteSchematic(Location loc, String schematic, boolean pasteAir) {
		worldEditHandler.pasteSchematic(loc, schematic, pasteAir);
	}
}
