package net.pwing.races.race.permission;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceExpChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class RacePermissionListener implements Listener {

    private PwingRaces plugin;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer());
    }

    @EventHandler
    public void onRaceExpChange(RaceExpChangeEvent event) {
        plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer());
    }

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
            plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer()), 20);
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        plugin.getRaceManager().getPermissionManager().removePermissions(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer()), 20);
    }

    @EventHandler
    public void onSkillpointReclaim(RaceReclaimSkillpointsEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
            plugin.getRaceManager().getPermissionManager().applyPermissions(event.getPlayer()), 20);
    }
}