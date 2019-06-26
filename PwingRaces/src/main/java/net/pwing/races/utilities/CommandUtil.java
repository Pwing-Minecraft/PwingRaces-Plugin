package net.pwing.races.utilities;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class CommandUtil {

    public static class RaceBukkitCommand extends Command implements PluginIdentifiableCommand {

        private CommandExecutor executor;
        private Plugin plugin;

        public RaceBukkitCommand(String name, String description, String usageMessage, List<String> aliases, Plugin plugin, CommandExecutor executor) {
            super(name, description, usageMessage, aliases);

            this.plugin = plugin;
            this.executor = executor;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return executor.onCommand(sender, this, commandLabel, args);
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }
    }

	public static void registerCommand(String prefix, Command command) {
		getCommandMap().register(prefix, command);
	}

	public static CommandMap getCommandMap() {
		Field field = null;
		try {
			field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
		    field.setAccessible(true);
		    return (CommandMap) field.get(Bukkit.getServer());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}

	    return null;
	}
}
