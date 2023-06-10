package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceExpChangeEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceExpChangeTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onRaceExpChange(RaceExpChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        if (event.getNewExp() >= event.getOldExp())
            triggerManager.runTriggers(player, "race-exp-gain");
        else
            triggerManager.runTriggers(player, "race-exp-lose");
    }
}
