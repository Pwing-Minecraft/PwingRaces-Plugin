package net.pwing.races.race.permission;

import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.race.permission.RacePermissionManager;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceExpChangeEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RacePermissionListener implements Listener {

    private RacePermissionManager permissionManager;

    public RacePermissionListener(RacePermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        permissionManager.applyPermissions(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        permissionManager.applyPermissions(event.getPlayer());
    }

    @EventHandler
    public void onRaceExpChange(RaceExpChangeEvent event) {
        permissionManager.applyPermissions(event.getPlayer());
    }

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        permissionManager.applyPermissions(event.getPlayer());
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        permissionManager.removePermissions(event.getPlayer());
    }
}