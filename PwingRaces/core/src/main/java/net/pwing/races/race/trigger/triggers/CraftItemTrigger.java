package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

@AllArgsConstructor
public class CraftItemTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;

        triggerManager.runTriggers((Player) event.getWhoClicked(), "craft-item");
        triggerManager.runTriggers((Player) event.getWhoClicked(), "craft-item " + event.getRecipe().getResult().getType().name().toLowerCase());
    }
}
