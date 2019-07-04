package net.pwing.races.race.trigger;

import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceExpChangeEvent;
import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class RaceTriggerListener implements Listener {

    private RaceTriggerManager triggerManager;

    public RaceTriggerListener(RaceTriggerManager triggerManager) {
        this.triggerManager = triggerManager;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "join");
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "quit");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "teleport");
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (event.isFlying())
            triggerManager.runTriggers(player, "fly");
        else
            triggerManager.runTriggers(player, "stop-fly");
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking())
            triggerManager.runTriggers(player, "sneak");
        else
            triggerManager.runTriggers(player, "stop-sneak");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        triggerManager.runTriggers(player, "take-damage");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        triggerManager.runTriggers(player, "take-damage " + event.getDamager().getType().name().toLowerCase());
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        triggerManager.runTriggers(player, "damage-entity");
        triggerManager.runTriggers(player, "damage-entity " + event.getDamager().getType().name().toLowerCase());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "move");

        for (BlockFace face : BlockFace.values())
            triggerManager.runTriggers(player, "block-relative " + face.name().toLowerCase() + " " + player.getLocation().getBlock().getRelative(face).getType().name().toLowerCase());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "block-break");
        triggerManager.runTriggers(player, "block-break " + event.getBlock().getType().name().toLowerCase());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "block-place");
        triggerManager.runTriggers(player, "block-place " + event.getBlock().getType().name().toLowerCase());
    }

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

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        triggerManager.runTriggers(player, "element-buy " + event.getPurchasedElement().getInternalName());
    }
}
