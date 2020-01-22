package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

@AllArgsConstructor
public class FlyTrigger implements Listener, RaceCondition {

    private RaceTriggerManager triggerManager;

    @Override
    public boolean check(Player player, String[] args) {
        return player.isFlying();
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (event.isFlying())
            triggerManager.runTriggers(player, "fly");
        else
            triggerManager.runTriggers(player, "stop-fly");
    }
}
