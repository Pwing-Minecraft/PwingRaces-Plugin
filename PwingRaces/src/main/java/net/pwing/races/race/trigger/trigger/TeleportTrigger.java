package net.pwing.races.race.trigger.trigger;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

@AllArgsConstructor
public class TeleportTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        triggerManager.runTriggers(event.getPlayer(), "teleport");
    }
}
