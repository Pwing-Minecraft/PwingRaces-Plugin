package net.pwing.races;

import lombok.Getter;

import net.pwing.races.api.PwingRacesAPI;
import net.pwing.races.api.module.RaceModuleManager;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.compat.CompatCodeHandlerDisabled;
import net.pwing.races.compat.ICompatCodeHandler;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.command.RaceExecutor;
import net.pwing.races.hooks.LibsDisguisesHook;
import net.pwing.races.hooks.LoreAttributesHook;
import net.pwing.races.hooks.MagicSpellsHook;
import net.pwing.races.hooks.MythicMobsHook;
import net.pwing.races.hooks.PlaceholderAPIHook;
import net.pwing.races.hooks.QuestsHook;
import net.pwing.races.hooks.VaultAPIHook;
import net.pwing.races.hooks.WorldEditHook;
import net.pwing.races.hooks.WorldGuardHook;
import net.pwing.races.module.PwingRaceModuleLoader;
import net.pwing.races.module.PwingRaceModuleManager;
import net.pwing.races.race.PwingRaceManager;
import net.pwing.races.task.RaceApplyAttributesTask;
import net.pwing.races.task.RaceSaveTask;
import net.pwing.races.task.RaceTriggerTickTask;
import net.pwing.races.utilities.MessageUtil;
import net.pwing.races.utilities.VersionUtil;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public class PwingRaces extends JavaPlugin {

    private static PwingRaces instance;

    private ICompatCodeHandler compatCodeHandler;

    private RaceManager raceManager;
    private RaceConfigurationManager configManager;
    private RaceModuleManager moduleManager;

    private VaultAPIHook vaultHook;
    private MagicSpellsHook magicSpellsHook;
    private MythicMobsHook mythicMobsHook;
    private QuestsHook questsHook;
    private LoreAttributesHook loreAttributesHook;
    private LibsDisguisesHook libsDisguisesHook;
    private WorldEditHook worldEditHook;
    private WorldGuardHook worldGuardHook;

    private boolean placeholderAPILoaded = false;
    private boolean pluginEnabled;

    @Override
    public void onEnable() {
        instance = this;

        generateDefaultConfigs();

        try {
            Class<? extends CompatCodeHandlerDisabled> compatClass = Class.forName("net.pwing.races.compat.CompatCodeHandler_" + VersionUtil.getNMSPackage()).asSubclass(CompatCodeHandlerDisabled.class);
            Constructor<? extends CompatCodeHandlerDisabled> compatConstructor = compatClass.getConstructor(PwingRaces.class);
            compatConstructor.setAccessible(true);

            compatCodeHandler = compatConstructor.newInstance(this);
            getLogger().info("Successfully found code compat handler class for version " + VersionUtil.getNMSPackage() + "!");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            getLogger().severe("Could not find compat code handler class for version " + VersionUtil.getNMSPackage() + ". This shouldn't be too big of a problem. If you are running the latest version of Minecraft currently, check Spigot for an updated version of the plugin. If not, ignore this.");
            compatCodeHandler = new CompatCodeHandlerDisabled(this);
            // ex.printStackTrace();
        }

        configManager = new RaceConfigurationManager(this);
        MessageUtil.initMessages("messages", configManager);

        try {
            moduleManager = new PwingRaceModuleManager(new PwingRaceModuleLoader(this));
        } catch (Throwable ex) {
            getLogger().warning("Error when loading modules! Please report this error!");
            ex.printStackTrace();
        }
        raceManager = new PwingRaceManager(this);

        PwingRacesAPI.setRaceManager(raceManager);
        PwingRacesAPI.setModuleManager(moduleManager);

        if (setupPlaceholderAPI()) {
            this.getLogger().info("PlaceholderAPI found, support for placeholders enabled.");
            placeholderAPILoaded = true;
        }

        vaultHook = new VaultAPIHook(MessageUtil.getMessage("currency-name-sing", ""), MessageUtil.getMessage("currency-name-plural", ""), this, "Vault");
        magicSpellsHook = new MagicSpellsHook(this, "MagicSpells");
        mythicMobsHook = new MythicMobsHook(this, "MythicMobs");
        loreAttributesHook = new LoreAttributesHook(this, "LoreAttributesRecoded");
        questsHook = new QuestsHook(this, "Quests");
        libsDisguisesHook = new LibsDisguisesHook(this, "LibsDisguises");
        worldEditHook = new WorldEditHook(this, "WorldEdit");
        worldGuardHook = new WorldGuardHook(this, "WorldGuard");

        registerCommands();

        // Just incase players are online during a reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            raceManager.registerPlayer(player);
            raceManager.setupPlayer(player);
        }

        long attributeRefreshTime = configManager.getAttributeRefreshTime();
        if (attributeRefreshTime > 0)
            getServer().getScheduler().runTaskTimerAsynchronously(this, new RaceApplyAttributesTask(raceManager.getAttributeManager()), attributeRefreshTime, attributeRefreshTime);

        getServer().getScheduler().runTaskTimerAsynchronously(this, new RaceTriggerTickTask(this), 1, 1);

        long autosave = configManager.getAutosave();
        if (autosave > 0)
            getServer().getScheduler().runTaskTimerAsynchronously(this, new RaceSaveTask(raceManager), autosave, autosave);

        pluginEnabled = true;

        new Metrics(this);
    }

    @Override
    public void onDisable() {
        // Just incase players are online during reload/restart
        Bukkit.getOnlinePlayers().forEach(raceManager::savePlayer);

        getLogger().info("Disabling modules...");
        moduleManager.getModules().values().forEach(moduleManager::disableModule);
    }

    public boolean reloadPlugin(){
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

            HandlerList.unregisterAll(this);

            reloadConfig();
            configManager = new RaceConfigurationManager(this);
            MessageUtil.initMessages("messages", configManager);

            // TODO: Refactor this ?
            ((PwingRaceManager) raceManager).initRaces();

            for (Player player : Bukkit.getOnlinePlayers()) {
                raceManager.registerPlayer(player);
                raceManager.setupPlayer(player);
                raceManager.getAttributeManager().applyAttributeBonuses(player);
            }

            pluginEnabled = true;
        } catch (Exception ex){
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

        File racesDir = new File(getDataFolder() + "/races");
        File playerDataDir = new File(getDataFolder() + "/playerdata");
        File hooksDir = new File(getDataFolder() + "/hooks");
        File modulesDir = new File(getDataFolder() + "/modules");
        File skilltreesDir = new File(getDataFolder() + "/skilltrees");

        if (!racesDir.exists())
            racesDir.mkdirs();

        if (!playerDataDir.exists())
            playerDataDir.mkdirs();

        if (!hooksDir.exists())
            hooksDir.mkdirs();

        if (!modulesDir.exists())
            modulesDir.mkdirs();

        if (!skilltreesDir.exists())
            skilltreesDir.mkdirs();

        if (!firstLoad)
            return;

        getLogger().info("It appears this is your first time running the plugin! Creating default files...");
        for (String race : new String[] {"dwarf", "human"}) {
            try {
                saveResource("races" + File.separator + race + ".yml", true);
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Could not create " + race + ".yml !!!");
            }
        }

        for (String skilltree : new String[] {"strength", "weaponry"}) {
            try {
                saveResource("skilltrees" + File.separator + skilltree + ".yml", true);
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

    public File getModuleFolder() {
        return new File(getDataFolder(), "modules");
    }

    public static PwingRaces getInstance() {
        return instance;
    }
}
