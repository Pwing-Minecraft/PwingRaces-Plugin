package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceReclaimItemsEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceReclaimItemsTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onReclaimItems(RaceReclaimItemsEvent event) {
        triggerManager.runTriggers(event.getPlayer(), "reclaim-race-items");
    }
}