package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceChangeTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "race-change");
        triggerManager.runTriggers(player, "race-change " + event.getNewRace().getName());
        triggerManager.runTriggers(player, "race-change-from " + event.getOldRace().getName());
    }
}
