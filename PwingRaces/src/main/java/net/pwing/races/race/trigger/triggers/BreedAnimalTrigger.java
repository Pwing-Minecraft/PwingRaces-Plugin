package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

@AllArgsConstructor
public class BreedAnimalTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getBreeder() instanceof Player))
            return;

        Player player = (Player) event.getBreeder();
        triggerManager.runTriggers(player, "breed-animal");
        triggerManager.runTriggers(player, "breed-animal " + event.getEntity().getType().name().toLowerCase());
    }
}
