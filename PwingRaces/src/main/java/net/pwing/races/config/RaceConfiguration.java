package net.pwing.races.config;

import java.io.File;
import java.io.IOException;

import lombok.AllArgsConstructor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

@AllArgsConstructor
public class RaceConfiguration {

	private File configFile;
	private FileConfiguration config;

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);

		try {
			config.save(configFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	public FileConfiguration getConfig() {
		return config;
	}
}
