package net.pwing.races.race.trigger.triggers;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@AllArgsConstructor
public class KillEntityTrigger implements Listener {

    private PwingRaces plugin;

    @EventHandler
    public void onKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "kill-entity");
        triggerManager.runTriggers(player, "kill-entity " + event.getEntity().getType().name().toLowerCase());

        if (!plugin.getMythicMobsHook().isHooked())
            return;

        if (MythicBukkit.inst().getAPIHelper().isMythicMob(event.getEntity())) {
            ActiveMob mythicMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(event.getEntity());

            triggerManager.runTriggers(player, "kill-mythicmob");
            triggerManager.runTriggers(player, "kill-mythicmob " + mythicMob.getType().getInternalName());
        }
    }
}
