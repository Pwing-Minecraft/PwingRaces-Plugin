package net.pwing.races.module;

import net.pwing.races.PwingRaces;
import net.pwing.races.config.RaceConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class RaceModule {

    protected FileConfiguration config;

    public void setupHookConfig() {
        File filePath = new File(PwingRaces.getInstance().getDataFolder() + "/hooks/" + getName() + "/");
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

    abstract void onEnable();

    abstract void onDisable();

    abstract String getName();

    abstract String getVersion();
}
