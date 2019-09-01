package net.pwing.races.menu;

import net.pwing.races.builder.ItemBuilder;

import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class ConfirmationMenu {

	private Inventory inv;
	private IConfirmationHandler handler;

	public ConfirmationMenu(Plugin plugin, IConfirmationHandler handler) {
		this(plugin, MessageUtil.getMessage("menu-confirmation", "Confirmation"), handler);
	}

	public ConfirmationMenu(Plugin plugin, String name, IConfirmationHandler handler) {
		this(plugin, name, MessageUtil.getMessage("menu-confirm-purchase", "&aConfirm Purchase"), MessageUtil.getMessage("menu-cancel-purchase", "&cCancel Purchase"), handler);
	}

	public ConfirmationMenu(Plugin plugin, String name, String yesMessage, String noMessage, IConfirmationHandler handler) {
		this.handler = handler;

		inv = Bukkit.createInventory(null, 27, name);
		inv.setItem(11, new ItemBuilder(Material.EMERALD_BLOCK).setName(ChatColor.GREEN + yesMessage).toItemStack());
		inv.setItem(15, new ItemBuilder(Material.REDSTONE_BLOCK).setName(ChatColor.RED + noMessage).toItemStack());

		registerListeners(plugin);
	}

	public void open(Player player) {
		player.openInventory(inv);
	}

	public Inventory toInventory() {
		return inv;
	}

	private void registerListeners(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();

				if (!event.getInventory().equals(inv))
					return;

				event.setCancelled(true);

				if (handler == null)
					return;

				if (event.getSlot() == 11)
					handler.onConfirm(player, event.getClick(), event.getCurrentItem());

				if (event.getSlot() == 15)
					handler.onDeny(player, event.getClick(), event.getCurrentItem());
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
