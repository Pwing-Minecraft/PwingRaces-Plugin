package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

@AllArgsConstructor
public class ConsumeItemTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.isCancelled())
            return;

        triggerManager.runTriggers(event.getPlayer(), "consume-item");
        triggerManager.runTriggers(event.getPlayer(), "consume-item " + event.getItem().getType().name().toLowerCase());
    }
}
