package net.pwing.races.hooks;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.pwing.races.PwingRaces;

public class VaultAPIHook extends PluginHook {

	private String econSingular;
	private String econPlural;

    private Economy economy;
    private Permission perms;

    private NumberFormat formatter = new DecimalFormat("#0.00");

    public VaultAPIHook(String econSingular, String econPlural, PwingRaces owningPlugin, String pluginName) {
    	super(owningPlugin, pluginName);

        this.econSingular = econSingular;
        this.econPlural = econPlural;
    }

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof Vault))
			return;

    	try {
            RegisteredServiceProvider<Economy> ecoProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            Economy economy = ecoProvider == null ? null : ecoProvider.getProvider();

            if (economy == null) {
                owningPlugin.getLogger().info("Vault found, but could not find economy plugin. Disabling economy hook.");
            } else {
            	this.economy = economy;

            	owningPlugin.getLogger().info("Vault found, using " + economy.getName() + " as an economy provider.");
            }

            RegisteredServiceProvider<Permission> permProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            Permission perms = permProvider == null ? null : permProvider.getProvider();
            if (perms == null) {
            	owningPlugin.getLogger().info("Vault found, but could not find permission plugin. Disabling permission hook.");
            } else {
            	this.perms = perms;

            	owningPlugin.getLogger().info("Vault found, using " + perms.getName() + " as a permission provider.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

    public boolean hasEconomy() {
    	return economy != null;
    }

    public boolean hasPermissions() {
    	return perms != null;
    }

    public double getBalance(Player player) {
    	if (!hasEconomy())
    		return 0;

    	return economy.getBalance(player);
    }

    public boolean hasBalance(Player player, double amount) {
    	if (!hasEconomy())
    		return false;

    	return economy.has(player, amount);
    }

    public String format(double amount) {
    	if (!hasEconomy())
    		return formatter.format(amount);

    	return economy.format(amount);
    }

    public boolean depositPlayer(Player player, double amount) {
    	if (!hasEconomy())
    		return false;

    	return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public boolean withdrawPlayer(Player player, double amount) {
    	if (!hasEconomy())
    		return false;

    	return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public String getCurrencyName(double amount) {
    	if (amount == 1)
    		return getCurrencyNameSing();

    	return getCurrencyNamePlural();
    }

    public String getCurrencyNameSing() {
    	String name = econSingular;
    	if (hasEconomy() && !economy.currencyNameSingular().isEmpty())
    		name = economy.currencyNameSingular();

    	return name;
    }

    public String getCurrencyNamePlural() {
    	String name = econPlural;
    	if (hasEconomy() && !economy.currencyNamePlural().isEmpty())
    		name = economy.currencyNamePlural();

    	return name;
    }

    public String getPrimaryGroup(Player player) {
    	if (!hasPermissions())
    		return "";

    	return perms.getPrimaryGroup(player);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
    	return hasPermission(sender, permission, false);
    }

    public boolean hasPermission(CommandSender sender, String permission, boolean ignoreOp) {
		if (!ignoreOp && sender.isOp())
			return true;

    	if (!hasPermissions())
			return sender.hasPermission(permission);

		return perms.has(sender, permission);
	}

    public boolean addPermission(Player player, String permission) {
    	if (!hasPermissions())
    		return false;

    	return perms.playerAddTransient(player, permission);
    }

    public boolean removePermission(Player player, String permission) {
    	if (!hasPermissions())
    		return false;

    	return perms.playerRemoveTransient(player, permission);
    }

    public boolean playerHasPermission(Player player, String permission) {
    	// no permission plugin installed, safe to
    	// use the bukkit manager here
    	if (!hasPermissions())
    		return hasPermission(player, permission);

    	return perms.playerHas(player, permission);
    }
}
