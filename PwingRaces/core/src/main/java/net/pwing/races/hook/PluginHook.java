package net.pwing.races.hook;

import net.pwing.races.PwingRaces;
import net.pwing.races.config.RaceConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class PluginHook {

    protected Plugin plugin;
    protected PwingRaces owningPlugin;

    protected RaceConfiguration hookConfig;

    public PluginHook(PwingRaces owningPlugin, String pluginName) {
        this.owningPlugin = owningPlugin;

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            owningPlugin.getLogger().info(pluginName + " not found, anything Pwing Races hooks for this plugin will be disabled.");
            return;
        }

        this.plugin = plugin;

        enableHook(owningPlugin, plugin);
    }

    public boolean isHooked() {
        return plugin != null;
    }

    public void setupHookConfig() {
        try {
            Path filePath = Paths.get(owningPlugin.getDataFolder().toString(), "hooks", plugin.getName());
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath);
            }
            Path file = Paths.get(filePath.toString(), plugin.getName() + "Config.yml");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
            hookConfig = new RaceConfiguration(file, YamlConfiguration.loadConfiguration(Files.newBufferedReader(file)));
        } catch (IOException ex) {
            owningPlugin.getLogger().severe("Could not create " + plugin.getName() + "Config.yml !!!");
            ex.printStackTrace();
        }
    }

    public RaceConfiguration getHookConfig() {
        return hookConfig;
    }

    public abstract void enableHook(PwingRaces owningPlugin, Plugin hook);
}
