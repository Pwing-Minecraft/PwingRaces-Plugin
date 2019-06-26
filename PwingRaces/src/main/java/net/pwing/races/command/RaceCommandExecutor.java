package net.pwing.races.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.pwing.races.PwingRaces;
import net.pwing.races.utilities.ItemUtil;

public class RaceCommandExecutor implements TabExecutor {

	protected PwingRaces plugin;

	private Map<String, Set<CommandWrapper>> commandMethods = new HashMap<String, Set<CommandWrapper>>();
	protected String parentCommand;

	public RaceCommandExecutor(PwingRaces plugin, String parentCommand) {
		this.plugin = plugin;
		this.parentCommand = parentCommand;

		registerCommands();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface RaceCommand {
		String[] commands() default { };
		String[] subCommands() default { };

		int minArgs() default 0;
		int maxArgs() default -1;

		boolean overrideDisabled() default false;

		boolean requiresOp() default false;
		String permissionNode() default "";

		String description() default "";
	}

	protected class CommandWrapper {
		protected Method method;
		protected String usage;

		public CommandWrapper(Method method, String usage) {
			this.method = method;
			this.usage = usage;
		}

		public RaceCommand getCommand() {
			return method.getAnnotation(RaceCommand.class);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sendNoArgumentMessage(sender);
			return true;
		}

		String subCommand = args[0];
		String args1 = args.length > 1 ? args[1] : null;

		List<CommandWrapper> wrappers = getCommandWrappers(subCommand, args1);

		for (CommandWrapper wrapper : wrappers) {
			if (wrapper != null) {
				int index = 1;

				if (!Arrays.asList(wrapper.getCommand().subCommands()).isEmpty())
	                index++;

				if (runCommand(sender, wrapper, Arrays.copyOfRange(args, index, args.length))) {
					return true;
				}
			}
		}

		if (wrappers.isEmpty())
			sendHelpMessage(sender);
		else
			sendUsageMessage(sender, wrappers.get(0).method);

		return true;
	}

	public void registerCommands() {
		for (Method method : getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(RaceCommand.class) && method.getReturnType() == boolean.class) {
				RaceCommand raceCommand = method.getAnnotation(RaceCommand.class);

				CommandWrapper wrapper = new CommandWrapper(method, getUsage(method));
				for (String cmd : raceCommand.commands()) {
					Set<CommandWrapper> wrappers = commandMethods.getOrDefault(cmd, new HashSet<CommandWrapper>());
					wrappers.add(wrapper);

					commandMethods.put(cmd, wrappers);
				}
			}
		}
	}

	public boolean runCommand(CommandSender sender, CommandWrapper wrapper, String[] args) {
		RaceCommand raceCommand = wrapper.getCommand();

		if (!plugin.isPluginEnabled() && !wrapper.getCommand().overrideDisabled()) {
			if (sender.isOp())
				sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " PwingRaces is disabled. This is most likely due to a configuration problem. Refer to the console for more information.");
			else
				sender.sendMessage(MessageUtil.getPrefix() + ChatColor.RED + " An error occurred when running this command, please contact an administrator!");

			return true;
		}

		try {
			if (raceCommand.requiresOp() && !sender.isOp()) {
				sender.sendMessage(MessageUtil.getReplacementMessage(MessageUtil.getMessage("no-permission-command", "%prefix% &cYou do not have permission to execute this command!")));
				return true;
			}

			if (!raceCommand.permissionNode().isEmpty() && !plugin.getVaultHook().hasPermission(sender, "pwingraces.command." + raceCommand.permissionNode())) {
				sender.sendMessage(MessageUtil.getReplacementMessage(MessageUtil.getMessage("no-permission-command", "%prefix% &cYou do not have permission to execute this command!")));
				return true;
			}

			Method method = wrapper.method;
			Class<?>[] requestedParams = method.getParameterTypes();
			Object[] params = new Object[requestedParams.length];
			int argCount = args.length;

			if (!(sender instanceof Player) && requestedParams[0].equals(Player.class))
				return false;

			params[0] = sender;

			if (requestedParams[requestedParams.length - 1].equals(String[].class)) {
				argCount = requestedParams.length - 2;
				int varParamCount = args.length - argCount;

				if (raceCommand.minArgs() > varParamCount)
					return false;

				if (raceCommand.maxArgs() != -1 && raceCommand.maxArgs() < varParamCount)
					return false;

				String[] varParams = varParamCount == 0 ? new String[0] : Arrays.copyOfRange(args, argCount, args.length);
				params[params.length - 1] = varParams;
			} else if (requestedParams.length - 1 != argCount) {
				return false;
			}

			boolean commandFound = true;
			for (int i = 0; i < argCount; i++) {
				Object obj = verifyArgument(sender, args[i], requestedParams[i + 1]);

				if (obj == null) {
					commandFound = false;
					break;
				} else {
					params[i + 1] = obj;
				}
			}

			if (commandFound) {
				return (boolean) method.invoke(this, params);
			}

			return true;
		} catch (RaceCommandException ex) {
			sender.sendMessage(MessageUtil.getReplacementMessage(MessageUtil.getMessage(ex.getMessage())));
			return true;
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}

		sendUsageMessage(sender, wrapper.method);
		return false;
	}

	public void sendNoArgumentMessage(CommandSender sender) {
		sendHelpMessage(sender);
	}

	public void sendHelpMessage(CommandSender sender) {
		sender.sendMessage(MessageUtil.getHeader());

		for (String command : commandMethods.keySet()) {
			for (CommandWrapper wrapper : commandMethods.get(command)) {
				if (plugin.getVaultHook().hasPermission(sender, "pwingraces.command." + wrapper.getCommand().permissionNode()))
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "/" + parentCommand + " " + wrapper.usage + ChatColor.WHITE + wrapper.getCommand().description());
			}
		}
	}

	public void sendUsageMessage(CommandSender sender, Method method) {
		sender.sendMessage(MessageUtil.getReplacementMessage(MessageUtil.getMessage("invalid-command-syntax", "%prefix% &cInvalid Syntax! Usage: %usage%.").replace("%usage%", "/" + parentCommand + " " + getUsage(method).trim())));
	}

	public List<CommandWrapper> getCommandWrappers(String command, String subCommand) {
		List<CommandWrapper> wrappers = new ArrayList<CommandWrapper>();

		for (String cmd : commandMethods.keySet()) {
			for (CommandWrapper wrapper : commandMethods.get(cmd)) {
				RaceCommand raceCommand = wrapper.getCommand();

				if (!Arrays.asList(raceCommand.commands()).contains(command))
					continue;

				if (Arrays.asList(raceCommand.subCommands()).isEmpty() || Arrays.asList(raceCommand.subCommands()).contains(subCommand)) {
					wrappers.add(wrapper);

				}
			}
		}

		return wrappers;
	}


	@SuppressWarnings("deprecation")
	protected Object verifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        switch (parameter.getSimpleName().toLowerCase()) {
			case "string":
				return arg;
			case "int":
				return Integer.parseInt(arg);
			case "double":
				return Double.parseDouble(arg);
			case "float":
				return Float.parseFloat(arg);
			case "boolean":
				switch (arg) {
					case "true":
					case "yes":
					case "on":
						return true;
					case "false":
					case "no":
					case "off":
						return false;
					default:
						return null;
				}
			case "material":
				return ItemUtil.fromString(arg);
			case "player":
				Player player = Bukkit.getPlayer(arg);
				if (player == null)
					throw new RaceCommandException("offline-player");

				return player;
			case "offlineplayer":
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);
				if (offlinePlayer == null)
					throw new RaceCommandException("player-invalid");

				return offlinePlayer;
			case "world":
				return Bukkit.getWorld(arg);
			default:
				return null;
		}
    }

	public String getUsage(Method method) {
		RaceCommand raceCommand = method.getAnnotation(RaceCommand.class);
		StringBuilder builder = new StringBuilder(raceCommand.commands().length > 0 ? raceCommand.commands()[0] + " " : "");
		int index = 1;

		if (raceCommand.subCommands().length > 0) {
			builder.append(raceCommand.subCommands()[0]).append(" ");
			index = 2;
		}

		Class<?>[] requestedParams = method.getParameterTypes();
		for (int i = index; i < requestedParams.length; i++) {
			Class<?> clazz = requestedParams[i];
			builder.append(getUsageString(clazz));
		}

		return builder.toString();
	}

	private String getUsageString(Class<?> parameter) {
		switch (parameter.getSimpleName().toLowerCase()) {
        	case "string":
                return "<string> ";
        	case "string[]":
        		return "[string...] ";
            case "int":
                return "<number> ";
            case "double":
                return "<number> ";
            case "float":
                return "<number> ";
            case "boolean":
                return "<true|false> ";
            case "material":
            	return "<material> ";
            case "player":
            case "offlineplayer":
                return "<player> ";
            case "world":
                return "<world> ";
            case "race":
                return "<race> ";
            default:
                return "<string> ";
        }
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completions = new ArrayList<String>();

		if (args.length == 1) {
			for (String cmd : commandMethods.keySet()) {
				CommandWrapper wrapper = commandMethods.get(cmd).iterator().next();
				RaceCommand raceCommand = wrapper.getCommand();

				if (!raceCommand.permissionNode().isEmpty() && !plugin.getVaultHook().hasPermission(sender, "pwingraces.command." + raceCommand.permissionNode()))
					continue;

				if (raceCommand.requiresOp() && !sender.isOp())
					continue;

				completions.add(cmd);
			}
		}

		if (args.length == 2) {
			if (!commandMethods.containsKey(args[0]))
				return null;

			for (CommandWrapper wrapper : commandMethods.get(args[0])) {
				RaceCommand raceCommand = wrapper.getCommand();

				if (!raceCommand.permissionNode().isEmpty() && !plugin.getVaultHook().hasPermission(sender, "pwingraces.command." + raceCommand.permissionNode()))
					continue;

				if (raceCommand.requiresOp() && !sender.isOp())
					continue;

				if (raceCommand.subCommands().length == 0)
					continue;

				for (String sub : raceCommand.subCommands())
					completions.add(sub);
			}
		}

		return completions;
	}
}
