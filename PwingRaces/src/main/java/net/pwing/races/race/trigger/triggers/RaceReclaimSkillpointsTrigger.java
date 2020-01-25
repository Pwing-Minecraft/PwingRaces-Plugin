package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public class RaceReclaimSkillpointsTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onReclaimSkillpoints(RaceReclaimSkillpointsEvent event) {
        triggerManager.runTriggers(event.getPlayer(), "reclaim-skillpoints");
    }
}