package net.pwing.races.compat;

import net.pwing.races.command.RaceCommandExecutor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ICompatCodeHandler {

	Enchantment getEnchantment(String name);
	int getDamage(ItemStack item);

	void setOwner(ItemStack item, String owner);
	void setDamage(ItemStack item, int damage);
	void setUnbreakable(ItemStack item, boolean unbreakable);
	void setColor(ItemStack item, Color color);
	void setCustomModelData(ItemStack item, int data);

	double getMaxHealth(Player player);
	void setMaxHealth(Player player, double maxHealth);
	ItemStack getItemInMainHand(Player player);

	boolean isBukkitAttribute(String name);
	String getAttributeName(String name);
	double getAttributeValue(Player player, String attribute);
	double getDefaultAttributeValue(Player player, String attribute);
	void setAttributeValue(Player player, String attribute, double amount);

	double getDamage(Arrow arrow);

	void setPickupStatus(Arrow arrow, String status);
	void setDamage(Arrow arrow, double damage);
}
