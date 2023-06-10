package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceLevelUpTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onLevelUp(RaceLevelUpEvent event) {
        Player player = event.getPlayer();
        if (event.getNewLevel() >= event.getOldLevel()) {
            triggerManager.runTriggers(player, "race-levelup");
            triggerManager.runTriggers(player, "race-levelup " + event.getNewLevel());
        } else {
            triggerManager.runTriggers(player, "race-leveldown");
            triggerManager.runTriggers(player, "race-leveldown " + event.getNewLevel());
        }
    }
}
