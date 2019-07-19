package net.pwing.races;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.pwing.races.api.PwingRacesAPI;
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
import net.pwing.races.race.PwingRaceManager;
import net.pwing.races.task.RaceSaveTask;
import net.pwing.races.task.RaceTriggerTickTask;
import net.pwing.races.utilities.MessageUtil;
import net.pwing.races.utilities.VersionUtil;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class PwingRaces extends JavaPlugin {

    private static PwingRaces instance;

    private ICompatCodeHandler compatCodeHandler;

    private RaceManager raceManager;
    private RaceConfigurationManager configManager;

    private VaultAPIHook vaultHook;
    private MagicSpellsHook magicSpellsHook;
    private MythicMobsHook mythicMobsHook;
    private QuestsHook questsHook;
    private LoreAttributesHook loreAttributesHook;
    private LibsDisguisesHook libsDisguisesHook;
    private WorldEditHook worldEditHook;
    private WorldGuardHook worldGuardHook;

    private boolean placeholderAPILoaded = false;
    private boolean isEnabled;

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

        raceManager = new PwingRaceManager(this);

        PwingRacesAPI.setRaceManager(raceManager);

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

        getServer().getScheduler().runTaskTimerAsynchronously(this, new RaceTriggerTickTask(this), 1, 1);

        long autosave = getConfig().getLong("settings.autosave");
        if (autosave > 0)
            getServer().getScheduler().runTaskTimerAsynchronously(this, new RaceSaveTask(raceManager), autosave, autosave);

        isEnabled = true;

        new Metrics(this);
    }

    @Override
    public void onDisable() {
        // Just incase players are online during reload/restart
        for (Player player : Bukkit.getOnlinePlayers()) {
            raceManager.savePlayer(player);
        }
    }

    public boolean reloadPlugin(){
        if (isEnabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                raceManager.savePlayer(player);
            }
        }

        isEnabled = false;
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
            }

            isEnabled = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // This is here so console isn't spammed when trying to find the stacktrace
        if (!isEnabled) {
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

    public VaultAPIHook getVaultHook() {
        return vaultHook;
    }

    public MagicSpellsHook getMagicSpellsHook() {
        return magicSpellsHook;
    }

    public MythicMobsHook getMythicMobsHook() {
        return mythicMobsHook;
    }

    public QuestsHook getQuestsHook() {
        return questsHook;
    }

    public LoreAttributesHook getLoreAttributesHook() {
        return loreAttributesHook;
    }

    public LibsDisguisesHook getLibsDisguisesHook() {
        return libsDisguisesHook;
    }

    public WorldEditHook getWorldEditHook() {
        return worldEditHook;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    public RaceManager getRaceManager() {
        return raceManager;
    }

    public RaceConfigurationManager getConfigManager() {
        return configManager;
    }

    public boolean isPlaceholderAPILoaded() {
        return placeholderAPILoaded;
    }

    public ClassLoader getPluginClassLoader() {
        return super.getClassLoader();
    }

    public File getModuleFolder() {
        return new File(getDataFolder(), "modules");
    }

    public ICompatCodeHandler getCompatCodeHandler() {
        return compatCodeHandler;
    }

    public boolean isPluginEnabled() {
        return isEnabled;
    }

    public static PwingRaces getInstance() {
        return instance;
    }
}
