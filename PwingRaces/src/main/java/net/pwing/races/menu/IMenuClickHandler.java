package net.pwing.races.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface IMenuClickHandler {

	void onClick(Player player, ClickType action, ItemStack item);
}
