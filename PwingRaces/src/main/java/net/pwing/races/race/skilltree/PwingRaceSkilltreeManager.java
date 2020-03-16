package net.pwing.races.race.skilltree;

import lombok.Getter;

import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PwingRaceSkilltreeManager implements RaceSkilltreeManager {

    private List<RaceSkilltree> skilltrees;

    public PwingRaceSkilltreeManager(Path path) {
        skilltrees = new ArrayList<>();
        initSkilltrees(path);
    }

    public void initSkilltrees(Path path) {
        if (!Files.isDirectory(path)) {
            return;
        }

        try {
            Files.walk(path).filter(file -> file.getFileName().toString().endsWith(".yml"))
                    .forEach(file -> {
                        try {
                            initSkilltree(file.getFileName().toString().replace(".yml", ""), YamlConfiguration.loadConfiguration(Files.newBufferedReader(file)));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
        } catch (IOException ex) {
            Bukkit.getLogger().warning("Failed to setup skilltree " + path.toString());
            ex.printStackTrace();
        }
    }

    public void initSkilltree(String regName, FileConfiguration config) {
        skilltrees.add(new PwingRaceSkilltree(regName, config));
    }
}
