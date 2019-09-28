package net.pwing.races.module;

import net.pwing.races.PwingRaces;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class RaceModule {

    protected boolean enabled = false;
    protected PwingRaces plugin = PwingRaces.getInstance();

    protected String name;
    protected String version;
    protected String author;

    protected FileConfiguration config;

    public void setupHookConfig() {
        File filePath = new File(PwingRaces.getInstance().getDataFolder() + "/modules/" + getName() + "/");
        if (!filePath.exists())
            filePath.mkdirs();

        File file = new File(filePath,  getName() + "Config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                PwingRaces.getInstance().getLogger().severe("Could not create " + getName() + "Config.yml !!!");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public FileConfiguration getModuleConfig() {
        return config;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
