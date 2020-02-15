package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

@AllArgsConstructor
public class DeathTrigger implements Listener {

    private PwingRaces plugin;

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
        if (!targetRacePlayer.getRace().isPresent())
            return;

        triggerManager.runTriggers(player, "killed-by " + targetRacePlayer.getRace().get().getName());
    }
}
