package net.pwing.races.builder;

import java.util.HashMap;
import java.util.Map;

import net.pwing.races.menu.IMenuClickHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class MenuBuilder {

	private String name;
	private int slots;

	private Inventory inv;

	private Map<Integer, IMenuClickHandler> handlerMap;

	public MenuBuilder(Plugin plugin) {
		this(plugin, "Races");
	}

	public MenuBuilder(Plugin plugin, String name) {
		this(plugin, name, 45);
	}

	public MenuBuilder(Plugin plugin, String name, int slots) {
		this.name = name;
		this.slots = slots;

		inv = Bukkit.createInventory(null, slots, name);
		handlerMap = new HashMap<Integer, IMenuClickHandler>();

		registerListeners(plugin);
	}

	public MenuBuilder addItem(ItemBuilder builder) {
		inv.addItem(builder.toItemStack());

		return this;
	}

	public MenuBuilder setItem(ItemBuilder builder, int slot) {
		inv.setItem(slot, builder.toItemStack());

		return this;
	}

	public MenuBuilder setItem(ItemStack stack, int slot) {
		inv.setItem(slot, stack);

		return this;
	}

	public void open(Player player) {
		player.openInventory(inv);
	}

	public String getName() {
		return name;
	}

	public int getInventorySize() {
		return slots;
	}

	public Inventory toInventory() {
		return inv;
	}

	public MenuBuilder addClickEvent(int slot, IMenuClickHandler handler) {
		handlerMap.put(slot, handler);

		return this;
	}

	private void registerListeners(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				if (!event.getInventory().equals(inv))
					return;

				event.setCancelled(true);
				if (handlerMap.containsKey(event.getSlot()))
					handlerMap.get(event.getSlot()).onClick(player, event.getClick(), event.getCurrentItem());
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
