package net.pwing.races.command;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceUnlockEvent;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // @RaceCommand(commands = "editor", description = "Open the race editor", permissionNode = "editor")
    // public boolean editor(Player player) {
    //     plugin.getRaceManager().getEditorManager().getRaceEditorMenu().openMenu(player);
    //     return true;
    // }

    @RaceCommand(commands = "info", description = "View your active race info.", permissionNode = "info")
    public boolean raceInfoActive(Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        if (!racePlayer.getRace().isPresent())
            return false;

        Race race = racePlayer.getRace().get();
        RaceData raceData = plugin.getRaceManager().getPlayerData(player, race);

        player.sendMessage(MessageUtil.getHeader());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Race: " + ChatColor.WHITE + race.getDisplayName());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + " / " + race.getRequiredExperience(raceData.getLevel()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Used Skillpoints: " + ChatColor.WHITE + raceData.getUsedSkillpoints());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Unused Skillpoints: " + ChatColor.WHITE + raceData.getUnusedSkillpoints());
        for (String str : race.getSkilltreeMap().values()) {
            RaceSkilltree skilltree = plugin.getRaceManager().getSkilltreeManager().getSkilltreeFromName(str).get();
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

    @RaceCommand(commands = "info", description = "View your race info.", permissionNode = "info")
    public boolean raceInfo(Player player, Race race) {
        RaceData raceData = plugin.getRaceManager().getPlayerData(player, race);

        player.sendMessage(MessageUtil.getHeader());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Race: " + ChatColor.WHITE + race.getDisplayName());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + " / " + race.getRequiredExperience(raceData.getLevel()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Used Skillpoints: " + ChatColor.WHITE + raceData.getUsedSkillpoints());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Unused Skillpoints: " + ChatColor.WHITE + raceData.getUnusedSkillpoints());
        for (String str : race.getSkilltreeMap().values()) {
            RaceSkilltree skilltree = plugin.getRaceManager().getSkilltreeManager().getSkilltreeFromName(str).get();
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
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Race: " + ChatColor.WHITE + race.getDisplayName());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + " / " + race.getRequiredExperience(raceData.getLevel()));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Used Skillpoints: " + ChatColor.WHITE + raceData.getUsedSkillpoints());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Unused Skillpoints: " + ChatColor.WHITE + raceData.getUnusedSkillpoints());
        for (String str : race.getSkilltreeMap().values()) {
            RaceSkilltree skilltree = plugin.getRaceManager().getSkilltreeManager().getSkilltreeFromName(str).get();
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

        RaceChangeEvent event = new RaceChangeEvent(player, racePlayer.getRace().orElse(null) /* is nullable */, race);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cannot-set-race", "%prefix% &cCannot set race.")));
            return true;
        }

        racePlayer.setRace(event.getNewRace());
        racePlayer.getRaceData(event.getNewRace()).setHasPlayed(true);

        // This needs to be called again for the new race as well
        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change");
        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change " + event.getNewRace().getName());

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
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-unlocked-new-race", "%prefix% Successfully unlocked %race% for %player_name%!").replace("%race%", ChatColor.stripColor(race.getDisplayName()))));
        if (player.isOnline()) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("unlocked-new-race", "%prefix% You have unlocked the race %race%!").replace("%race%", ChatColor.stripColor(race.getDisplayName()))));
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

        this.plugin.getRaceManager().getAttributeManager().removeAttributeBonuses(player);

        plugin.getRaceManager().savePlayer(player);
        plugin.getRaceManager().registerPlayer(player, true);
        plugin.getRaceManager().setupPlayer(player);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("reset-race-data", "%prefix% Successfully reset the race data for %player_name%!")).replace("%player_name%", player.getName()));
        return true;
    }

    @RaceCommand(commands = "reload", description = "Reload the race plugin.", permissionNode = "reload", overrideDisabled = true)
    public boolean reloadRaces(CommandSender sender) {
        sender.sendMessage(MessageUtil.getPrefix() + " Reloading PwingRaces...");
        long startTime = System.currentTimeMillis();
        if (!plugin.reloadPlugin()) {
            sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " An error occurred when reloading the plugin. Please check the console for more details!");
            return true;
        }

        double completeTime = (System.currentTimeMillis() - startTime) / 1000D;
        sender.sendMessage(MessageUtil.getPrefix() + " PwingRaces successfully reloaded (" + new DecimalFormat("#.###").format(completeTime) + "s)!");
        return true;
    }

    @RaceCommand(commands = "set", subCommands = "skillpoint", description = "Set a player's skillpoints.", permissionNode = "set.skillpoint")
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

    @RaceCommand(commands = "add", subCommands = "skillpoint", description = "Add to a player's skillpoint amount.", permissionNode = "add.skillpoint")
    public boolean addSkillpoints(CommandSender sender, OfflinePlayer player, Race race, int skillpoints) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setUnusedSkillpoints(raceData.getUnusedSkillpoints() + skillpoints);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-skillpoint-message", "%prefix% &aYou have set %player_name%'s skillpoints to %skillpoints%.")).replace("%skillpoints%", String.valueOf(raceData.getUnusedSkillpoints())));
        return true;
    }

    @RaceCommand(commands = "remove", subCommands = "skillpoint", description = "Remove from a player's skillpoint amount.", permissionNode = "remove.skillpoint")
    public boolean removeSkillpoints(CommandSender sender, OfflinePlayer player, Race race, int skillpoints) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setUnusedSkillpoints(raceData.getUnusedSkillpoints() - skillpoints);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-skillpoint-message", "%prefix% &aYou have set %player_name%'s skillpoints to %skillpoints%.")).replace("%skillpoints%", String.valueOf(raceData.getUnusedSkillpoints())));
        return true;
    }

    @RaceCommand(commands = "set", subCommands = "level", description = "Set a player's level.", permissionNode = "set.level")
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

    @RaceCommand(commands = "add", subCommands = "level", description = "Add to a player's level.", permissionNode = "add.level")
    public boolean addLevel(CommandSender sender, OfflinePlayer player, Race race, int level) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setLevel(raceData.getLevel() + level);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-level-message", "%prefix% &aYou have set %player_name%'s level to %level%.")).replace("%level%", String.valueOf(raceData.getLevel() + level)));
        return true;
    }

    @RaceCommand(commands = "remove", subCommands = "level", description = "Remove from a player's level.", permissionNode = "remove.level")
    public boolean removeLevel(CommandSender sender, OfflinePlayer player, Race race, int level) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setLevel(raceData.getLevel() - level);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-level-message", "%prefix% &aYou have set %player_name%'s level to %level%.")).replace("%level%", String.valueOf(raceData.getLevel() - level)));
        return true;
    }

    @RaceCommand(commands = "set", subCommands = "exp", description = "Set a player's race exp.", permissionNode = "set.exp")
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

    @RaceCommand(commands = "add", subCommands = "exp", description = "Add to a player's race exp.", permissionNode = "add.exp")
    public boolean addExp(CommandSender sender, OfflinePlayer player, Race race, int exp) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setExperience(raceData.getExperience() + exp);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-exp-message", "%prefix% &aYou have set %player_name%'s race exp to %exp%.")).replace("%exp%", String.valueOf(raceData.getExperience() + exp)));
        return true;
    }

    @RaceCommand(commands = "remove", subCommands = "exp", description = "Remove from a player's race exp.", permissionNode = "remove.exp")
    public boolean removeExp(CommandSender sender, OfflinePlayer player, Race race, int exp) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        RaceData raceData = racePlayer.getRaceData(race);
        raceData.setExperience(raceData.getExperience() - exp);
        sender.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("set-exp-message", "%prefix% &aYou have set %player_name%'s race exp to %exp%.")).replace("%exp%", String.valueOf(raceData.getExperience() - exp)));
        return true;
    }

    @RaceCommand(commands = "level", description = "View your race level.", permissionNode = "level")
    public boolean raceLevel(Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return true;
        }

        if (!racePlayer.getRace().isPresent())
            return false;

        Race race = racePlayer.getRace().get();
        RaceData raceData = plugin.getRaceManager().getPlayerData(player, race);

        player.sendMessage(MessageUtil.getHeader());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Race: " + ChatColor.WHITE + race.getDisplayName());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Level: " + ChatColor.WHITE + raceData.getLevel());
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience: " + ChatColor.WHITE + raceData.getExperience() + " / " + race.getRequiredExperience(raceData.getLevel()));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Experience Until Level Up: " + ChatColor.WHITE + (race.getRequiredExperience(raceData.getLevel()) - raceData.getExperience()));
        return true;
    }

    @Override
    protected Object verifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("race")) {
            Optional<Race> race = plugin.getRaceManager().getRaceFromName(arg);
            if (!race.isPresent())
                throw new RaceCommandException("invalid-race");

            return race.get();
        }

        return super.verifyArgument(sender, arg, parameter);
    }

    @Override
    protected List<String> verifyTabComplete(String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("race")) {
            Race[] races = plugin.getRaceManager().getRaces().toArray(new Race[0]);
            return Stream.of(races).map(Race::getName).collect(Collectors.toList());
        }

        return super.verifyTabComplete(arg, parameter);
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
