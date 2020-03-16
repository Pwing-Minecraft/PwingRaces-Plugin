package net.pwing.races.config;

import lombok.AllArgsConstructor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
public class RaceConfiguration {

	private Path configFile;
	private FileConfiguration config;

	public void reloadConfig() {
		try {
			config = YamlConfiguration.loadConfiguration(Files.newBufferedReader(configFile));
			config.save(configFile.toFile());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			config.save(configFile.toFile());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Path getConfigPath() {
		return configFile;
	}

	public FileConfiguration getConfig() {
		return config;
	}
}
