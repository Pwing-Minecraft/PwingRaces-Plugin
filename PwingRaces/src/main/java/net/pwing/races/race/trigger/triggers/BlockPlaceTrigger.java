package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@AllArgsConstructor
public class BlockPlaceTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "block-place");
        triggerManager.runTriggers(player, "block-place " + event.getBlock().getType().name().toLowerCase());
    }
}
