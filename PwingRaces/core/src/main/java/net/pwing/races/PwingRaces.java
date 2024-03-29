package net.pwing.races;

import lombok.Getter;
import net.pwing.races.api.module.RaceModuleManager;
import net.pwing.races.command.RaceExecutor;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.hook.AureliumSkillsHook;
import net.pwing.races.hook.LibsDisguisesHook;
import net.pwing.races.hook.LoreAttributesHook;
import net.pwing.races.hook.MagicSpellsHook;
import net.pwing.races.hook.MythicMobsHook;
import net.pwing.races.hook.PlaceholderAPIHook;
import net.pwing.races.hook.QuestsHook;
import net.pwing.races.hook.VaultAPIHook;
import net.pwing.races.hook.WorldEditHook;
import net.pwing.races.hook.WorldGuardHook;
import net.pwing.races.module.PwingRaceModuleLoader;
import net.pwing.races.module.PwingRaceModuleManager;
import net.pwing.races.race.PwingRaceManager;
import net.pwing.races.util.MessageUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

@Getter
public class PwingRaces extends JavaPlugin {

    private static PwingRaces instance;

    private PwingRaceManager raceManager;
    private RaceConfigurationManager configManager;
    private RaceModuleManager moduleManager;

    private VaultAPIHook vaultHook;
    private AureliumSkillsHook aureliumSkillsHook;
    private MagicSpellsHook magicSpellsHook;
    private MythicMobsHook mythicMobsHook;
    private QuestsHook questsHook;
    private LoreAttributesHook loreAttributesHook;
    private LibsDisguisesHook libsDisguisesHook;
    private WorldEditHook worldEditHook;
    private WorldGuardHook worldGuardHook;

    private boolean placeholderAPILoaded = false;
    private boolean pluginEnabled;

    private Consumer<PwingRaces> onEnable;
    private Consumer<PwingRaces> onDisable;

    public PwingRaces() {
    }

    public PwingRaces(Consumer<PwingRaces> onEnable, Consumer<PwingRaces> onDisable) {
        this.onEnable = onEnable;
        this.onDisable = onDisable;
    }

    @Override
    public void onEnable() {
        instance = this;

        generateDefaultConfigs();

        configManager = new RaceConfigurationManager(this);
        MessageUtil.initMessages("messages", configManager);

        try {
            moduleManager = new PwingRaceModuleManager(new PwingRaceModuleLoader(this));
        } catch (Throwable ex) {
            getLogger().warning("Error when loading modules! Please report this error!");
            ex.printStackTrace();
        }

        if (setupPlaceholderAPI()) {
            this.getLogger().info("PlaceholderAPI found, support for placeholders enabled.");
            placeholderAPILoaded = true;
        }

        vaultHook = new VaultAPIHook(MessageUtil.getMessage("currency-name-sing", ""), MessageUtil.getMessage("currency-name-plural", ""), this, "Vault");
        aureliumSkillsHook = new AureliumSkillsHook(this, "AureliumSkills");
        magicSpellsHook = new MagicSpellsHook(this, "MagicSpells");
        mythicMobsHook = new MythicMobsHook(this, "MythicMobs");
        loreAttributesHook = new LoreAttributesHook(this, "LoreAttributesRecoded");
        questsHook = new QuestsHook(this, "Quests");
        libsDisguisesHook = new LibsDisguisesHook(this, "LibsDisguises");
        worldEditHook = new WorldEditHook(this, "WorldEdit");
        worldGuardHook = new WorldGuardHook(this, "WorldGuard");

        raceManager = new PwingRaceManager(this);

        registerCommands();

        // Just incase players are online during a reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            raceManager.registerPlayer(player);
            raceManager.setupPlayer(player);
        }

        long autosave = configManager.getAutosave();
        if (autosave > 0)
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.getServer().getOnlinePlayers().forEach(raceManager::savePlayer), autosave, autosave);

        if (onEnable != null) {
            onEnable.accept(this);
        }

        pluginEnabled = true;

        new Metrics(this);
    }

    @Override
    public void onDisable() {
        // Just incase players are online during reload/restart
        Bukkit.getOnlinePlayers().forEach(raceManager::savePlayer);
        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("Disabling modules...");
        moduleManager.getModules().values().forEach(moduleManager::disableModule);

        if (onDisable != null) {
            onDisable.accept(this);
        }
    }

    public boolean reloadPlugin() {
        if (pluginEnabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                raceManager.savePlayer(player);
                raceManager.getAttributeManager().removeAttributeBonuses(player);
            }
        }

        pluginEnabled = false;
        try {
            raceManager.getRacePlayerMap().clear();
            configManager = null;

            reloadConfig();
            configManager = new RaceConfigurationManager(this);
            MessageUtil.initMessages("messages", configManager);

            raceManager.reloadRaces();

            for (Player player : Bukkit.getOnlinePlayers()) {
                raceManager.registerPlayer(player);
                raceManager.setupPlayer(player);
                raceManager.getAttributeManager().applyAttributeBonuses(player);
            }

            pluginEnabled = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // This is here so console isn't spammed when trying to find the stacktrace
        if (!pluginEnabled) {
            HandlerList.unregisterAll(this);
            return false;
        }

        return true;
    }

    public void registerCommands() {
        RaceExecutor raceExecutor = new RaceExecutor(this);
        getCommand("race").setExecutor(raceExecutor);

        /*
        for (Race race : raceManager.getRaces()) {
            RaceCommandExecutor indvRaceExecutor = race.getExecutor();
            RaceBukkitCommand raceCommand = new RaceBukkitCommand(race.getName(), "Command for the " + race.getName() + " race.",
                    "/" + race.getName(), new ArrayList<String>(), this, indvRaceExecutor);

            CommandUtil.registerCommand(race.getName().toLowerCase(), raceCommand);
        }
        */
    }

    public void generateDefaultConfigs() {
        boolean firstLoad = false;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
            firstLoad = true;
        }

        saveDefaultConfig();

        Path racesDir = Paths.get(getDataFolder().toString(), "races");
        Path playerDataDir = Paths.get(getDataFolder().toString(), "playerdata");
        Path hooksDir = Paths.get(getDataFolder().toString(), "hooks");
        Path modulesDir = Paths.get(getDataFolder().toString(), "modules");
        Path skilltreesDir = Paths.get(getDataFolder().toString(), "skilltrees");

        try {
            if (Files.notExists(racesDir)) {
                Files.createDirectories(racesDir);
            }

            if (Files.notExists(playerDataDir)) {
                Files.createDirectories(playerDataDir);
            }
            if (Files.notExists(hooksDir)) {
                Files.createDirectories(hooksDir);
            }
            if (Files.notExists(modulesDir)) {
                Files.createDirectories(modulesDir);
            }
            if (Files.notExists(skilltreesDir)) {
                Files.createDirectories(skilltreesDir);
            }
        } catch (IOException ex) {
            this.getLogger().warning("Failed to create directories!");
            ex.printStackTrace();
        }
        if (!firstLoad) {
            return;
        }
        getLogger().info("It appears this is your first time running the plugin! Creating default files...");
        for (String race : new String[] {"dwarf", "human"}) {
            try {
                saveResource("races" + FileSystems.getDefault().getSeparator() + race + ".yml", true);
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Could not create " + race + ".yml !!!");
            }
        }

        for (String skilltree : new String[] {"strength", "weaponry"}) {
            try {
                saveResource("skilltrees" + FileSystems.getDefault().getSeparator() + skilltree + ".yml", true);
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Could not create " + skilltree + ".yml !!!");
            }
        }
    }

    private boolean setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
            return false;

       return new PlaceholderAPIHook(this).register();
    }

    public ClassLoader getPluginClassLoader() {
        return super.getClassLoader();
    }

    public Path getModuleFolder() {
        return Paths.get(getDataFolder().toString(), "modules");
    }

    public static PwingRaces getInstance() {
        return instance;
    }
}
