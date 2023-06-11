package net.pwing.races.util;

import net.pwing.races.PwingRaces;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for interaction input
 *
 * @author Redned
 */
public class InteractionInputs {

    public static abstract class ChatInput {

        /**
         * Constructs a new ChatInput instance
         *
         * @param player the player to receive the chat input from
         */
        public ChatInput(Player player) {
            Listener listener = new Listener() {

                @EventHandler
                public void onChat(AsyncPlayerChatEvent event) {
                    if (!player.equals(event.getPlayer())) {
                        return;
                    }

                    event.setCancelled(true);
                    if (!isValidChatInput(event.getMessage())) {
                        player.sendMessage(ChatColor.RED + "Invalid input, please try again.");
                        return;
                    }

                    String message = ChatColor.stripColor(event.getMessage());

                    // Run task synchronously since chat is async
                    Bukkit.getScheduler().runTask(PwingRaces.getInstance(), () -> {
                        onChatInput(message);
                    });

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, PwingRaces.getInstance());
        }

        /**
         * Runs when the player enters text in chat
         *
         * @param input the text the player inputted
         */
        public abstract void onChatInput(String input);

        /**
         * Checks if the input is valid
         *
         * @param input the input to check
         * @return true if the input is valid, false otherwise
         */
        public boolean isValidChatInput(String input) {
            return true;
        }
    }

    public static abstract class InventoryInput {

        /**
         * Constructs a new InventoryInput instance
         *
         * @param player the player to receive the inventory input from
         */
        public InventoryInput(Player player) {
            Listener listener = new Listener() {

                @EventHandler
                public void onInteract(InventoryClickEvent event) {
                    if (!player.equals(event.getWhoClicked())) {
                        return;
                    }

                    if (!player.getInventory().equals(event.getClickedInventory())) {
                        player.sendMessage(ChatColor.RED + "Interacted with inventory that was not own.. cancelling item selection.");
                        HandlerList.unregisterAll(this);
                        return;
                    }

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                        return;
                    }

                    onInventoryInteract(event.getCurrentItem());

                    event.setCancelled(true);
                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, PwingRaces.getInstance());
        }

        /**
         * Runs when the player interacts with an item in
         * their inventory
         *
         * @param item the item the player interacted with
         */
        public abstract void onInventoryInteract(ItemStack item);
    }
}