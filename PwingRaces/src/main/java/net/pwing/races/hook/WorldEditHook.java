package net.pwing.races.hook;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.pwing.races.PwingRaces;
import net.pwing.races.hook.worldedit.WorldEditHandler;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class WorldEditHook extends PluginHook {

	private WorldEditHandler worldEditHandler;

	public WorldEditHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof WorldEditPlugin)) {
			return;
		}

		owningPlugin.getLogger().info("WorldEdit found, schematic hook enabled.");

		worldEditHandler = new WorldEditHandler();
	}

	public void pasteSchematic(Location loc, String schematic, boolean pasteAir) {
		if (!isHooked()) {
			return;
		}

		worldEditHandler.pasteSchematic(loc, schematic, pasteAir);
	}
}
