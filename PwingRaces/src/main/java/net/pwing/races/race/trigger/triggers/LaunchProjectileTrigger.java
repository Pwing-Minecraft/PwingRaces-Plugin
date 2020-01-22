package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

@AllArgsConstructor
public class LaunchProjectileTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity().getShooter();
        triggerManager.runTriggers(player, "launch-projectile");
        triggerManager.runTriggers(player, "launch-projectile " + event.getEntity().getType().name().toLowerCase());
    }
}
