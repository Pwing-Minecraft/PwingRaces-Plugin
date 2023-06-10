package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

@AllArgsConstructor
public class BurnTrigger implements Listener, RaceCondition {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onBurn(EntityCombustEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        triggerManager.runTriggers(player, "burn");
    }

    @Override
    public boolean check(Player player, String[] args) {
        return player.getFireTicks() > 0;
    }
}
