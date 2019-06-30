package net.pwing.races.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.pwing.races.PwingRaces;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.race.PwingRacePlayer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {

    private static Map<String, String> messages;

    public static void initMessages(String configPath, RaceConfigurationManager configManager) {
        messages = new HashMap<String, String>();

        YamlConfiguration config = configManager.getMessageConfig().getConfig();
        // Adding default from version 1.0.9
        config.set(configPath + ".set-skillpoint-message", "%prefix% &aYou have set %player_name%'s skillpoints to %skillpoints%.");
        config.set(configPath + ".set-level-message", "%prefix% &aYou have set %player_name%'s level to %level%.");
        config.set(configPath + ".set-exp-message", "%prefix% &aYou have set %player_name%'s race exp to %exp%.");
        configManager.getMessageConfig().saveConfig();

        for (String str : config.getConfigurationSection(configPath).getKeys(false))
            messages.put(str, config.getString(configPath + "." + str));
    }

    public static void sendMessage(Player player, String message, String fallback) {
        if (fallback.equals("") || fallback.isEmpty())
            return;

        for (String str : messages.keySet())
            message = message.replace("%" + str + "%", messages.get(str));

        if (message.contains("\n")) {
            for (String msg : message.split("\n")) {
                player.sendMessage(getPlaceholderMessage(player, getMessage(msg, fallback)));
            }

        } else {
            player.sendMessage(getPlaceholderMessage(player, getMessage(message, fallback)));
        }
    }

    public static String getReplacementMessage(String message) {
        message = message.replace("%prefix%", getPrefix());
        message = message.replace("%header%", getHeader());

        return colorMessage(message);
    }

    public static String getPlaceholderMessage(OfflinePlayer player, String message) {
        if (PwingRaces.getInstance().isPlaceholderAPILoaded())
            message = PlaceholderAPI.setPlaceholders(player, message);

        // If PlaceholderAPI isn't installed
        if (player != null && player.isOnline()) {
            message = message.replace("%player_name%", player.getName());

            PwingRacePlayer racePlayer = PwingRaces.getInstance().getRaceManager().getRacePlayer(player);
            if (racePlayer != null && racePlayer.getActiveRace() != null)
                message = message.replace("%race%", racePlayer.getActiveRace().getName());
        }

        return getReplacementMessage(message);
    }

    public static String colorMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getMessage(String messageKey, String fallback) {
        String message = fallback;
        if (messages.containsKey(messageKey))
            message = getMessage(messageKey);

        return colorMessage(message);
    }

    public static String getHeader() {
        return getMessage("header", "&5-------------------&7[ &d&lPwing Races &7]&5-------------------");
    }

    public static String getPrefix() {
        return getMessage("prefix", "&7[Pwing Races]&a");
    }

    public static String getMessage(String str) {
        return messages.get(str);
    }
}
