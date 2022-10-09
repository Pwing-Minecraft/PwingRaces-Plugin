package net.pwing.races.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface IConfirmationHandler {

	boolean onConfirm(Player player, ClickType action, ItemStack item);
	void onDeny(Player player, ClickType action, ItemStack item);
}
