package net.pwing.races.util.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.util.item.ItemBuilder;

import net.pwing.races.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ConfirmationMenu {

	private Inventory inv;
	private IConfirmationHandler handler;

	public ConfirmationMenu(PwingRaces plugin, IConfirmationHandler handler) {
		this(plugin, MessageUtil.getMessage("menu-confirmation", "Confirmation"), handler);
	}

	public ConfirmationMenu(PwingRaces plugin, String name, IConfirmationHandler handler) {
		this(plugin, name, MessageUtil.getMessage("menu-confirm-purchase", "&aConfirm Purchase"), MessageUtil.getMessage("menu-cancel-purchase", "&cCancel Purchase"), handler);
	}

	public ConfirmationMenu(PwingRaces plugin, String name, String yesMessage, String noMessage, IConfirmationHandler handler) {
		this.handler = handler;

		inv = Bukkit.createInventory(null, 27, name);
		inv.setItem(11, ItemBuilder.builder(Material.EMERALD_BLOCK).name(ChatColor.GREEN + yesMessage).build());
		inv.setItem(15, ItemBuilder.builder(Material.REDSTONE_BLOCK).name(ChatColor.RED + noMessage).build());

		registerListeners(plugin);
	}

	public void open(Player player) {
		player.openInventory(inv);
	}

	private void registerListeners(PwingRaces plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();

				if (!event.getInventory().equals(inv))
					return;

				event.setCancelled(true);

				if (handler == null)
					return;

				if (event.getSlot() == 11) {
					boolean success = handler.onConfirm(player, event.getClick(), event.getCurrentItem());
					Sound sound = success ? plugin.getConfigManager().getSuccessSound() : plugin.getConfigManager().getDenySound();
					if (sound != null) {
						player.playSound(player.getLocation(), sound, 1f, 1f);
					}
				}

				if (event.getSlot() == 15) {
					handler.onDeny(player, event.getClick(), event.getCurrentItem());
					Sound sound = plugin.getConfigManager().getDenySound();
					if (sound != null) {
						player.playSound(player.getLocation(), sound, 1f, 1f);
					}
				}
			}

			@EventHandler
			public void onClose(InventoryCloseEvent event) {
				if (event.getInventory().equals(inv)) {
					HandlerList.unregisterAll(this);
				}
			}
		}, plugin);
	}
}
