package net.pwing.races.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
	private final ItemStack stack;

	private ItemBuilder(Material material) {
		stack = new ItemStack(material);
	}

	private ItemBuilder(ItemStack stack) {
		this.stack = stack;
	}

	public ItemBuilder material(Material material) {
		stack.setType(material);
		return this;
	}

	public ItemBuilder amount(int amount) {
		stack.setAmount(amount);
		return this;
	}

	public ItemBuilder durability(int durability) {
		ItemMeta meta = stack.getItemMeta();
		if (meta instanceof Damageable damageable) {
			damageable.setDamage(durability);
		}

		stack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder unbreakable(boolean unbreakable) {
		ItemMeta meta = stack.getItemMeta();
		meta.setUnbreakable(unbreakable);
		stack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder name(String name) {
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder lore(String lore) {
		String[] split = lore.split("\n");
		List<String> lores = new ArrayList<>() {
			{

				for (String s : split) {
					add(ChatColor.translateAlternateColorCodes('&', s));
				}
			}
		};

		return lore(lores);
	}

	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemBuilder lore(List<String> lore) {
		ItemMeta im = stack.getItemMeta();

		List<String> newLore = new ArrayList<>();
		lore.forEach(loreStr -> newLore.add(ChatColor.translateAlternateColorCodes('&', loreStr)));
		im.setLore(newLore);

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder itemFlags(ItemFlag... flag) {
		ItemMeta im = stack.getItemMeta();
		im.addItemFlags(flag);

		stack.setItemMeta(im);
		return this;
	}

	public ItemBuilder potionEffect(PotionEffect effect) {
		if (stack.getItemMeta() instanceof PotionMeta im) {
			im.addCustomEffect(effect, true);
			stack.setItemMeta(im);
		}
		return this;
	}

	public ItemBuilder owner(PlayerProfile owner) {
		if (stack.getItemMeta() instanceof SkullMeta meta) {
			meta.setOwnerProfile(owner);
			stack.setItemMeta(meta);
		}
		return this;
	}

	public ItemBuilder color(Color color) {
		if (stack.getItemMeta() instanceof LeatherArmorMeta im) {
			im.setColor(color);
			stack.setItemMeta(im);
		}

		if (stack.getItemMeta() instanceof PotionMeta im) {
			im.setColor(color);
			stack.setItemMeta(im);
		}
		return this;
	}

	public ItemBuilder enchantment(Enchantment ench, int level) {
		stack.addUnsafeEnchantment(ench, level);
		return this;
	}

	public ItemBuilder customModelData(int data) {
		ItemMeta im = stack.getItemMeta();
		im.setCustomModelData(data);

		stack.setItemMeta(im);
		return this;
	}
	
	public static ItemBuilder builder(Material material) {
		return new ItemBuilder(material);
	}
	
	public static ItemBuilder builder(ItemStack stack) {
		return new ItemBuilder(stack.clone());
	}

	public ItemBuilder clone() {
		return ItemBuilder.builder(build());
	}

	public ItemStack build() {
		return stack;
	}
}

