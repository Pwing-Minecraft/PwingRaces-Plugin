package net.pwing.races.race.trigger.trigger;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@AllArgsConstructor
public class SneakTrigger implements Listener, RaceCondition {

    private RaceTriggerManager triggerManager;

    @Override
    public boolean check(Player player, String[] args) {
        return player.isSneaking();
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking())
            triggerManager.runTriggers(event.getPlayer(), "sneak");
        else
            triggerManager.runTriggers(event.getPlayer(), "stop-sneak");
    }
}
