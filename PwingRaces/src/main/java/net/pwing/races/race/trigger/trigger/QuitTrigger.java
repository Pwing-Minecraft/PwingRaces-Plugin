package net.pwing.races.race.trigger.trigger;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class QuitTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        triggerManager.runTriggers(event.getPlayer(), "quit");
    }
}
