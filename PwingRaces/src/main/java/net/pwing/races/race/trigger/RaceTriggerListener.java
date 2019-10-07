package net.pwing.races.race.trigger;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceExpChangeEvent;
import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class RaceTriggerListener implements Listener {

    private PwingRaces plugin;

    public RaceTriggerListener(PwingRaces plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "join");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "quit");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "teleport");
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.isFlying())
            triggerManager.runTriggers(player, "fly");
        else
            triggerManager.runTriggers(player, "stop-fly");
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
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
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "take-damage");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "take-damage " + event.getDamager().getType().name().toLowerCase());
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "damage-entity");
        triggerManager.runTriggers(player, "damage-entity " + event.getEntity().getType().name().toLowerCase());

        if (!plugin.getMythicMobsHook().isHooked())
            return;

        if (MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity())) {
            ActiveMob mythicMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(event.getEntity());

            triggerManager.runTriggers(player, "damage-mythicmob");
            triggerManager.runTriggers(player, "damage-mythicmob " + mythicMob.getType().getInternalName());
        }
    }

    @EventHandler
    public void onKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        if (!(event.getEntity().getKiller() instanceof Player))
            return;

        Player player = event.getEntity().getKiller();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "kill-entity");
        triggerManager.runTriggers(player, "kill-entity " + event.getEntity().getType().name().toLowerCase());

        if (!plugin.getMythicMobsHook().isHooked())
            return;

        if (MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity())) {
            ActiveMob mythicMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(event.getEntity());

            triggerManager.runTriggers(player, "kill-mythicmob");
            triggerManager.runTriggers(player, "kill-mythicmob " + mythicMob.getType().getInternalName());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "move");
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "health-regen");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "block-break");
        triggerManager.runTriggers(player, "block-break " + event.getBlock().getType().name().toLowerCase());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "block-place");
        triggerManager.runTriggers(player, "block-place " + event.getBlock().getType().name().toLowerCase());
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "race-change");
        triggerManager.runTriggers(player, "race-change " + event.getNewRace().getName());
    }

    @EventHandler
    public void onLevelUp(RaceLevelUpEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.getNewLevel() >= event.getOldLevel()) {
            triggerManager.runTriggers(player, "race-levelup");
            triggerManager.runTriggers(player, "race-levelup " + event.getNewLevel());
        } else {
            triggerManager.runTriggers(player, "race-leveldown");
            triggerManager.runTriggers(player, "race-leveldown " + event.getNewLevel());
        }
    }

    @EventHandler
    public void onBurn(EntityCombustEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "burn");
    }

    @EventHandler
    public void onRaceExpChange(RaceExpChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
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
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "element-buy " + event.getPurchasedElement().getInternalName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "death");

        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (!(lastDamageCause instanceof EntityDamageByEntityEvent))
            return;

        Entity damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
        triggerManager.runTriggers(player, "killed-by " + damager.getType().name().toLowerCase());
        if (!(damager instanceof Player))
               return;

        RacePlayer targetRacePlayer = plugin.getRaceManager().getRacePlayer((Player) damager);
        Race targetRace = targetRacePlayer.getActiveRace();
        if (targetRace == null)
            return;

        triggerManager.runTriggers(player, "killed-by" + targetRace.getName());
    }
}
