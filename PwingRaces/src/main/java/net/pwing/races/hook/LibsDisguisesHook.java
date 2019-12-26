package net.pwing.races.hook;

import net.pwing.races.hook.disguises.DisguiseHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import me.libraryaddict.disguise.LibsDisguises;
import net.pwing.races.PwingRaces;

public class LibsDisguisesHook extends PluginHook {

	private DisguiseHandler disguiseHandler;

	public LibsDisguisesHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof LibsDisguises))
			return;

		owningPlugin.getLogger().info("LibsDisguises found, disguise hook enabled.");
		disguiseHandler = new DisguiseHandler();
	}

	public void disguiseEntity(Entity entity, EntityType type) {
		if (!isHooked())
			return;

		disguiseHandler.disguiseEntity(entity, type);
	}

	public void undisguiseEntity(Entity entity) {
		if (!isHooked())
			return;

		disguiseHandler.undisguiseEntity(entity);
	}
}
