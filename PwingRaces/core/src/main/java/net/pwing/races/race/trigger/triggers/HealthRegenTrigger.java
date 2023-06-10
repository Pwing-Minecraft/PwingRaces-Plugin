package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

@AllArgsConstructor
public class HealthRegenTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        Player player = (Player) event.getEntity();
        triggerManager.runTriggers(player, "health-regen");
    }
}
