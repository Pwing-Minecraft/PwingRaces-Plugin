package net.pwing.races.util.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.pwing.races.PwingRaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder {

	private ItemStack stack;

	public ItemBuilder() {
		stack = new ItemStack(Material.AIR);
	}

	public ItemBuilder(Material material) {
		stack = new ItemStack(material);
	}

	public ItemBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public ItemBuilder(SafeMaterialData data) {
		this.stack = new ItemStack(data.getMaterial(), 1, (short) data.getData());
	}

	public ItemBuilder setType(Material material) {
		stack.setType(material);
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		stack.setAmount(amount);
		return this;
	}

	public ItemBuilder setDurability(int durability) {
		PwingRaces.getInstance().getCompatCodeHandler().setDamage(stack, durability);
		return this;
	}

	public ItemBuilder setUnbreakable(boolean unbreakable) {
		ItemMeta meta = stack.getItemMeta();
		meta.setUnbreakable(unbreakable);
		stack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder setLore(String lore) {
		String[] split = lore.split("\n");
		List<String> lores = new ArrayList<String>() {
			private static final long serialVersionUID = 4437712182410853273L; {

			for (String s : split) {
				add(ChatColor.translateAlternateColorCodes('&', s));
			}
		}};

		return setLore(lores);
	}

	public ItemBuilder setLore(String... lore) {
		return setLore(Arrays.asList(lore));
	}

	public ItemBuilder setLore(List<String> lore) {
		ItemMeta im = stack.getItemMeta();

		List<String> newLore = new ArrayList<>();
		lore.forEach(loreStr -> newLore.add(ChatColor.translateAlternateColorCodes('&', loreStr)));
		im.setLore(newLore);

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder addItemFlag(ItemFlag... flag) {
		ItemMeta im = stack.getItemMeta();
		im.addItemFlags(flag);

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder addPotionEffect(PotionEffect effect) {
		if (stack.getItemMeta() instanceof PotionMeta) {
			PotionMeta im = (PotionMeta) stack.getItemMeta();
			im.addCustomEffect(effect, true);
			stack.setItemMeta(im);
		}
		return this;
	}

	public ItemBuilder setOwner(String owner) {
		if (stack.getItemMeta() instanceof SkullMeta) {
			SkullMeta meta = (SkullMeta) stack.getItemMeta();
			meta.setOwner(owner);
			stack.setItemMeta(meta);
		}
		return this;
	}

	public ItemBuilder setColor(Color color) {
		if (stack.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta im = (LeatherArmorMeta) stack.getItemMeta();
			im.setColor(color);
			stack.setItemMeta(im);
		}
		if (stack.getItemMeta() instanceof PotionMeta) {
			PotionMeta im = (PotionMeta) stack.getItemMeta();
			im.setColor(color);
			stack.setItemMeta(im);
		}
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment ench, int level) {
		stack.addUnsafeEnchantment(ench, level);
		return this;
	}

	public String getDisplayName() {
		return stack.getItemMeta().getDisplayName();
	}

	public ItemBuilder setCustomModelData(int data) {
		PwingRaces.getInstance().getCompatCodeHandler().setCustomModelData(stack, data);
		return this;
	}

	public ItemBuilder clone() {
		return new ItemBuilder(toItemStack());
	}

	public ItemStack toItemStack() {
		return stack;
	}
}

