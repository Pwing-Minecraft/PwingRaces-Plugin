package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.RaceManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@AllArgsConstructor
public class TakeDamageTrigger implements Listener {

    private RaceManager raceManager;

    @EventHandler
    public void onTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        raceManager.getTriggerManager().runTriggers(player, "take-damage");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        raceManager.getTriggerManager().runTriggers(player, "take-damage " + event.getDamager().getType().name().toLowerCase());

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        raceManager.getRacePlayer(damager).getRace().ifPresent(race ->
                raceManager.getTriggerManager().runTriggers(player, "take-damage " + race.getName()));
    }
}
