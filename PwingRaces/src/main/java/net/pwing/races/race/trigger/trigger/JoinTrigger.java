package net.pwing.races.race.trigger.trigger;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class JoinTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        triggerManager.runTriggers(event.getPlayer(), "join");
    }
}
