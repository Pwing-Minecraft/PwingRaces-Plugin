package net.pwing.races.hooks;

import java.io.File;
import java.io.IOException;

import net.pwing.races.config.RaceConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import net.pwing.races.PwingRaces;

public abstract class PluginHook {

	protected Plugin plugin;
	protected PwingRaces owningPlugin;

	protected RaceConfiguration hookConfig;

	public PluginHook(PwingRaces owningPlugin, String pluginName) {
		this.owningPlugin = owningPlugin;

		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null) {
			owningPlugin.getLogger().info(pluginName + " not found, anything that hooks into this plugin will be disabled.");
			return;
		}

		this.plugin = plugin;

		enableHook(owningPlugin, plugin);
	}

	public boolean isHooked() {
		return plugin != null;
	}

	public void setupHookConfig() {
		File filePath = new File(owningPlugin.getDataFolder() + "/hooks/" + plugin.getName() + "/");
		if (!filePath.exists())
			filePath.mkdirs();

		File file = new File(filePath,  plugin.getName() + "Config.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				owningPlugin.getLogger().severe("Could not create " + plugin.getName() + "Config.yml !!!");
				e.printStackTrace();
			}
		}

		hookConfig = new RaceConfiguration(file, YamlConfiguration.loadConfiguration(file));
	}

	public RaceConfiguration getHookConfig() {
		return hookConfig;
	}

	public abstract void enableHook(PwingRaces owningPlugin, Plugin hook);

	public Plugin getOwningPlugin() {
		return owningPlugin;
	}
}
