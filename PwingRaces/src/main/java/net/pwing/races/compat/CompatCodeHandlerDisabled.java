package net.pwing.races.compat;

import net.pwing.races.PwingRaces;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class CompatCodeHandlerDisabled implements ICompatCodeHandler {

	private PwingRaces plugin;

	public CompatCodeHandlerDisabled(PwingRaces plugin) {
		this.plugin = plugin;
	}

	@Override
	public Enchantment getEnchantment(String name) {
		return Enchantment.getByName(name);
	}

	@Override
	public int getDamage(ItemStack item) {
		return item.getDurability();
	}

	@Override
	public void setDamage(ItemStack item, int damage) {
		item.setDurability((short) damage);
	}

	// Not implemented until 1.14
	@Override
	public void setCustomModelData(ItemStack item, int data) {

	}

	@Override
	public double getDamage(Arrow arrow) {
		return 0;
	}

	@Override
	public void setDamage(Arrow arrow, double damage) {
		// arrow.spigot().setDamage(damage);
	}

	@Override
	public CompletableFuture<String> getHeadURL(String player) {
		return null;
	}
}
