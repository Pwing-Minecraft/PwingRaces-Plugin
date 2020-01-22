package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
public class UseInventoryTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onUseInventory(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        // Anvil repairs
        if (inventory instanceof AnvilInventory) {
            if (event.getSlotType() != InventoryType.SlotType.RESULT) {
                return;
            }

            // Ensure nothing is empty
            if (inventory.getItem(0) == null || inventory.getItem(0).getType() == Material.AIR)
                return;

            if (inventory.getItem(1) == null || inventory.getItem(1).getType() == Material.AIR)
                return;

            if (inventory.getItem(2) == null || inventory.getItem(2).getType() == Material.AIR)
                return;

            triggerManager.runTriggers(player, "use-anvil");
        }
        triggerManager.runTriggers(player, "use-inventory");
    }
}
