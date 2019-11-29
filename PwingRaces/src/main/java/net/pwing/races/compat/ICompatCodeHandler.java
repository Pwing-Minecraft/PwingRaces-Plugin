package net.pwing.races.compat;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public interface ICompatCodeHandler {

	Enchantment getEnchantment(String name);
	int getDamage(ItemStack item);

	void setOwner(ItemStack item, String owner);
	void setDamage(ItemStack item, int damage);
	void setUnbreakable(ItemStack item, boolean unbreakable);
	void setColor(ItemStack item, Color color);
	void setCustomModelData(ItemStack item, int data);

	double getDefaultAttributeValue(Player player, String attribute);

	double getDamage(Arrow arrow);

	void setPickupStatus(Arrow arrow, String status);
	void setDamage(Arrow arrow, double damage);

	CompletableFuture<String> getHeadURL(String player);
}
