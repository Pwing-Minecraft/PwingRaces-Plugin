package net.pwing.races.module;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.module.RaceModule;
import net.pwing.races.api.module.RaceModuleLoader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PwingRaceModuleLoader implements RaceModuleLoader {

    private PwingRaces plugin;
    protected Map<String, RaceModule> modules;

    public PwingRaceModuleLoader(PwingRaces plugin) {
        this.plugin = plugin;
        this.modules = new HashMap<>();

        loadModules();
    }

    @Override
    public void loadModules() {
        File directory = plugin.getModuleFolder();
        if (!directory.exists())
            directory.mkdir();

        for (File moduleFile : directory.listFiles()) {
            if (!moduleFile.getName().toLowerCase().endsWith(".jar"))
                continue;

            try {
                ZipFile zipFile = new ZipFile(moduleFile);
                ZipEntry zipEntry = zipFile.getEntry("module.yml");

                if (zipEntry == null || zipEntry.isDirectory()) {
                    plugin.getLogger().warning("Module " + moduleFile.getName() + " is missing a module.yml file! Please contact the author!");
                    return;
                }

                URL[] urls = new URL[]{moduleFile.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls, plugin.getPluginClassLoader());

                zipFile.stream().forEach((file -> {
                    if (file.getName().endsWith(".class")) {
                        try {
                            InputStream inputStream = zipFile.getInputStream(file);
                            byte[] buffer = new byte[inputStream.available()];
                            inputStream.read(buffer);
                            classLoader.loadClass(getClassCanonicalName(file).replace(".class", ""));
                            inputStream.close();
                        } catch (Exception ex) {
                            plugin.getLogger().warning("Error when setting up module " + moduleFile.getName() + "! Please contact the module author!");
                            ex.printStackTrace();
                        }
                    }
                }));

                InputStream moduleYml = zipFile.getInputStream(zipEntry);
                FileConfiguration moduleConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(moduleYml));
                plugin.getLogger().info("Loading module " + moduleConfig.getString("name") + "...");

                RaceModuleWrapper module = new RaceModuleWrapper((RaceModule) Class.forName(moduleConfig.getString("main"), true, classLoader).newInstance());
                module.name = moduleConfig.getString("name");
                module.version = moduleConfig.getString("version");
                module.author = moduleConfig.getString("author");
                module.plugin = plugin;
                plugin.getLogger().info("Loaded module " + module.name + "!");

                module.onEnable();
                plugin.getLogger().info("Enabled module " + module.name + "!");
            } catch (Exception ex) {
                plugin.getLogger().warning("Error loading module " + moduleFile.getName() + "! Please contact the module author!");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void enableModule(RaceModule module) {
        module.onEnable();
        module.setEnabled(true);
        modules.put(module.getName(), module);
    }

    @Override
    public void disableModule(RaceModule module) {
        module.setEnabled(false);
        modules.remove(module.getName());
        module.onDisable();
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String getClassCanonicalName(ZipEntry entry) {
        String entryName = entry.getName();
        if (getFileExtension(entryName).toLowerCase().endsWith("class")) {
            return entryName.replaceAll("/", ".");
        } else {
            return null;
        }
    }
}
