package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

@AllArgsConstructor
public class TameAnimalTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getOwner() instanceof Player))
            return;

        Player player = (Player) event.getOwner();
        triggerManager.runTriggers(player, "tame-animal");
        triggerManager.runTriggers(player, "tame-animal " + event.getEntity().getType().name().toLowerCase());
    }
}
