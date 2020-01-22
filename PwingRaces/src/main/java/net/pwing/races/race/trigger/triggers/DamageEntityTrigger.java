package net.pwing.races.race.trigger.triggers;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AllArgsConstructor
public class DamageEntityTrigger implements Listener {

    private PwingRaces plugin;

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
}
