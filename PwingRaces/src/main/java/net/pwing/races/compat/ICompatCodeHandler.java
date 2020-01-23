package net.pwing.races.compat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public interface ICompatCodeHandler {

	Enchantment getEnchantment(String name);
	int getDamage(ItemStack item);

	void setDamage(ItemStack item, int damage);
	void setCustomModelData(ItemStack item, int data);

	double getDamage(Arrow arrow);
	void setDamage(Arrow arrow, double damage);

	CompletableFuture<String> getHeadURL(String player);
}
