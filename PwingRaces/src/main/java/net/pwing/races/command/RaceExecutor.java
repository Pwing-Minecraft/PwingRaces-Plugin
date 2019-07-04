package net.pwing.races.command;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceMenu;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceUnlockEvent;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.utilities.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaceExecutor extends RaceCommandExecutor {

    private PwingRaces plugin;

    public RaceExecutor(PwingRaces plugin) {
        super(plugin, "race");

        this.plugin = plugin;
    }

    @RaceCommand(commands = "help", description = "View this help page.", permissionNode = "help")
    public boolean helpCommand(CommandSender sender) {
        super.sendHelpMessage(sender);
        return true;
    }

    @RaceCommand(commands = "info", description = "View your race info.", permissionNode = "info")
    public boolean raceInfo(Player player, Race race) {
        RaceData raceData = plugin.getRaceManager().getPlayerData(player, race);

        player.sendMessage(MessageUtil.getHeader());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + "/" + race.getRequiredExperience(raceData.getLevel()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Used Skillpoints: " + ChatColor.WHITE + raceData.getUsedSkillpoints());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Unused Skillpoints: " + ChatColor.WHITE + raceData.getUnusedSkillpoints());
        for (String str : race.getSkilltreeMap().values()) {
            RaceSkilltree skilltree = plugin.getRaceManager().getSkilltreeManager().getSkilltreeFromName(str);
            player.sendMessage(ChatColor.WHITE + skilltree.getName() + " Skilltree: ");

            for (RaceSkilltreeElement elem : skilltree.getElements()) {
                ChatColor color = ChatColor.RED;
                if (raceData.hasPurchasedElement(skilltree.getInternalName(), elem.getInternalName()))
                    color = ChatColor.GREEN;

                player.sendMessage(ChatColor.WHITE + "- " + color  + elem.getTitle());
            }
        }

        return true;
    }

    @RaceCommand(commands = "info", description = "View a player's race info.", permissionNode = "info.others")
    public boolean raceInfoOthers(CommandSender sender, OfflinePlayer player, Race race) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);

        sender.sendMessage(MessageUtil.getHeader());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + " / " + race.getRequiredExperience(raceData.getLevel()));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Used Skillpoints: " + ChatColor.WHITE + raceData.getUsedSkillpoints());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Unused Skillpoints: " + ChatColor.WHITE + raceData.getUnusedSkillpoints());
        for (String str : race.getSkilltreeMap().values()) {
            RaceSkilltree skilltree = plugin.getRaceManager().getSkilltreeManager().getSkilltreeFromName(str);
            sender.sendMessage(ChatColor.WHITE + skilltree.getName() + " Skilltree: ");

            for (RaceSkilltreeElement elem : skilltree.getElements()) {
                ChatColor color = ChatColor.RED;
                if (raceData.hasPurchasedElement(skilltree.getInternalName(), elem.getInternalName()))
                    color = ChatColor.GREEN;

                sender.sendMessage(ChatColor.WHITE + "- " + color  + elem.getTitle());
            }
        }

        return true;
    }

    @RaceCommand(commands = "set", description = "Set a player's race.", permissionNode = "set")
    public boolean setRace(CommandSender sender, Player player, Race race) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " An error occurred when trying to set " + player.getName() + "'s race. Failed to retrieve data.");
            return true;
        }

        RaceChangeEvent event = new RaceChangeEvent(player, racePlayer.getActiveRace(), race);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cannot-set-race", "%prefix% &cCannot set race.")));
            return true;
        }

        racePlayer.setActiveRace(event.getNewRace());
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-active-race", "%prefix% Successfully set %player_name%'s race to %race%!")));
        return true;
    }

    @RaceCommand(commands= "unlock", description = "Unlock a race for a player.", permissionNode = "unlock")
    public boolean unlockRace(CommandSender sender, OfflinePlayer player, Race race) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceUnlockEvent event = new RaceUnlockEvent(player, race);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cannot-unlock-race", "%prefix% &cCannot unlock race.")));
            return true;
        }

        racePlayer.getRaceData(event.getRace()).setUnlocked(true);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-unlocked-new-race", "%prefix% Successfully unlocked %race% for %player_name%!").replace("%race%", race.getName())));
        if (player.isOnline()) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("unlocked-new-race", "%prefix% You have unlocked the race %race%!").replace("%race%", race.getName())));
        }
        return true;
    }

    @RaceCommand(commands = "reset", description = "Reset all of a player's race data.", permissionNode = "reset")
    public boolean resetRace(CommandSender sender, Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " An error occurred when trying to set " + player.getName() + "'s race. Failed to retrieve data.");
            return true;
        }

        plugin.getRaceManager().savePlayer(player);
        plugin.getRaceManager().registerPlayer(player, true);
        plugin.getRaceManager().setupPlayer(player);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("reset-race-data", "%prefix% Successfully reset the race data for %player_name%!")).replace("%player_name%", player.getName()));
        return true;
    }

    @RaceCommand(commands = "reload", description = "Reload the race plugin.", permissionNode = "reload", overrideDisabled = true)
    public boolean reloadRaces(CommandSender sender) {
        sender.sendMessage(MessageUtil.getPrefix() + " Reloading PwingRaces...");

        if (!plugin.reloadPlugin()) {
            sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " An error occured when reloading the plugin. Please check the console for more details!");
            return true;
        }

        sender.sendMessage(MessageUtil.getPrefix() + " PwingRaces successfully reloaded!");
        return true;
    }

    @RaceCommand(commands = "skillpoint", subCommands = "set", description = "Set a player's skillpoints", permissionNode = "skillpoint.set")
    public boolean setSkillpoints(CommandSender sender, OfflinePlayer player, Race race, int skillpoints) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setUnusedSkillpoints(skillpoints);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-skillpoint-message", "%prefix% &aYou have set %player_name%'s skillpoints to %skillpoints%.")).replace("%skillpoints%", String.valueOf(skillpoints)));
        return true;
    }

    @RaceCommand(commands = "level", subCommands = "set", description = "Set a player's level.", permissionNode = "level.set")
    public boolean setLevel(CommandSender sender, OfflinePlayer player, Race race, int level) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setLevel(level);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-level-message", "%prefix% &aYou have set %player_name%'s level to %level%.")).replace("%level%", String.valueOf(level)));
        return true;
    }

    @RaceCommand(commands = "exp", subCommands = "set", description = "Set a player's race exp.", permissionNode = "exp.set")
    public boolean setExp(CommandSender sender, OfflinePlayer player, Race race, int exp) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setExperience(exp);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-exp-message", "%prefix% &aYou have set %player_name%'s race exp to %exp%.")).replace("%exp%", String.valueOf(exp)));
        return true;
    }

    @Override
    protected Object verifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("race")) {
            Race race = plugin.getRaceManager().getRaceFromName(arg);
            if (race == null)
                throw new RaceCommandException("invalid-race");

            return race;
        }

        return super.verifyArgument(sender, arg, parameter);
    }

    // Instead of a help message, open the race GUI.
    @Override
    public void sendNoArgumentMessage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            super.sendHelpMessage(sender);
            return;
        }

        if (!plugin.getVaultHook().hasPermission(sender, "pwingraces.command.race")) {
            sender.sendMessage(MessageUtil.getReplacementMessage(MessageUtil.getMessage("no-permission-command", "%prefix% &cYou do not have permission to execute this command!")));
            return;
        }

        Player player = (Player) sender;
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld())) {
            MessageUtil.sendMessage(player, "races-disabled-in-world", "%prefix% &cRaces are not enabled in this world!");
            return;
        }

        RaceMenu menu = plugin.getRaceManager().getRaceMenu();
        menu.openMenu(player);
    }
}
