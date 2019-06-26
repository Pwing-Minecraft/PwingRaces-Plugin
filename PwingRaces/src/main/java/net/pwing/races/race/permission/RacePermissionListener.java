package net.pwing.races.race.permission;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pwing.races.events.RaceElementPurchaseEvent;
import net.pwing.races.events.RaceExpChangeEvent;

public class RacePermissionListener implements Listener {

	private RacePermissionManager permissionManager;

	public RacePermissionListener(RacePermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		permissionManager.applyPermissions(event.getPlayer());
	}

	@EventHandler
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
}