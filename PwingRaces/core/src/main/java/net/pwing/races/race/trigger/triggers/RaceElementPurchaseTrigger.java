package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceElementPurchaseTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "element-buy " + event.getPurchasedElement().getInternalName());
    }
}
