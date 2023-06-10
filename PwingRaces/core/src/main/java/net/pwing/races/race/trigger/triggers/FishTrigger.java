package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

@AllArgsConstructor
public class FishTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.isCancelled())
            return;

        triggerManager.runTriggers(event.getPlayer(), "fish");
        if (event.getCaught() != null) {
            triggerManager.runTriggers(event.getPlayer(), "fish " + event.getCaught().getType().name().toLowerCase());
        }
        if (event.getCaught() instanceof Item) {
            Item item = (Item) event.getCaught();
            triggerManager.runTriggers(event.getPlayer(), "fish " + item.getItemStack().getType().name().toLowerCase());
        }
    }
}
