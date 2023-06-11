package net.pwing.races.util.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.util.item.ItemBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MenuBuilder {
	private final Inventory inv;

	private final Map<Integer, Pair<Sound, IMenuClickHandler>> handlerMap = new HashMap<>();

	private MenuBuilder(Plugin plugin, String name) {
		this(plugin, name, 45);
	}

	private MenuBuilder(Plugin plugin, String name, int slots) {
		this.inv = Bukkit.createInventory(null, slots, name);

		registerListeners(plugin);
	}

	public MenuBuilder item(ItemBuilder builder, int slot) {
		inv.setItem(slot, builder.build());
		return this;
	}

	public MenuBuilder item(ItemStack stack, int slot) {
		inv.setItem(slot, stack);
		return this;
	}

	public MenuBuilder item(ItemBuilder builder, int slot, IMenuClickHandler handler) {
		this.item(builder, slot);
		this.clickEvent(slot, handler);
		return this;
	}

	public MenuBuilder item(ItemStack stack, int slot, IMenuClickHandler handler) {
		this.item(stack, slot);
		this.clickEvent(slot, handler);
		return this;
	}

	public void open(Player player) {
		player.openInventory(inv);
	}

	public Inventory build() {
		return inv;
	}

	public MenuBuilder clickEvent(int slot, IMenuClickHandler handler) {
		return clickEvent(slot, PwingRaces.getInstance().getConfigManager().getClickSound(), handler);
	}

	public MenuBuilder clickEvent(int slot, boolean clickCondition, IMenuClickHandler handler) {
		Sound sound = clickCondition ? PwingRaces.getInstance().getConfigManager().getClickSound() : PwingRaces.getInstance().getConfigManager().getDenySound();
		handlerMap.put(slot, Pair.of(sound, handler));
		return this;
	}

	public MenuBuilder clickEvent(int slot, Sound clickSound, IMenuClickHandler handler) {
		handlerMap.put(slot, Pair.of(clickSound, handler));
		return this;
	}
	
	public static MenuBuilder builder(Plugin plugin, String name) {
		return new MenuBuilder(plugin, name);
	}
	
	public static MenuBuilder builder(Plugin plugin, String name, int slots) {
		return new MenuBuilder(plugin, name, slots);
	}

	private void registerListeners(Plugin plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				if (!event.getInventory().equals(inv))
					return;

				event.setCancelled(true);

				if (handlerMap.containsKey(event.getSlot())) {
					Pair<Sound, IMenuClickHandler> pair = handlerMap.get(event.getSlot());
					if (pair.getValue() != null) {
						pair.getValue().onClick(player, event.getClick(), event.getCurrentItem());
					}

					if (pair.getKey() != null) {
						player.playSound(player.getLocation(), pair.getKey(), 1f, 1f);
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
