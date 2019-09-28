package net.pwing.races.module;

import net.pwing.races.api.module.RaceModule;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RaceModuleWrapper extends RaceModule {

    private RaceModule module;

    protected JavaPlugin plugin;

    protected boolean enabled = false;

    protected String name;
    protected String version;
    protected String author;

    protected FileConfiguration config;

    public RaceModuleWrapper(RaceModule module) {
        this.module = module;
    }

    @Override
    public void onEnable() {
        module.onEnable();
    }

    @Override
    public void onDisable() {
        module.onDisable();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public FileConfiguration getModuleConfig() {
        return config;
    }
}
