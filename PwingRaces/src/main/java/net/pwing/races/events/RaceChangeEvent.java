package net.pwing.races.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.pwing.races.race.PwingRace;

public class RaceChangeEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private PwingRace oldRace;
	private PwingRace newRace;

	private boolean cancelled;

	public RaceChangeEvent(Player player, PwingRace oldRace, PwingRace newRace) {
		super(player);
		this.oldRace = oldRace;
		this.newRace = newRace;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public PwingRace getOldRace() {
		return oldRace;
	}

	public PwingRace getNewRace() {
		return newRace;
	}

	public void setNewRace(PwingRace newRace) {
		this.newRace = newRace;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}