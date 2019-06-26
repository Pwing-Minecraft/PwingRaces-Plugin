package net.pwing.races.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.pwing.races.race.Race;

public class RaceLevelUpEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private Race race;

	private int oldLevel;
	private int newLevel;

	private boolean cancelled;

	public RaceLevelUpEvent(Player player, Race race, int oldLevel, int newLevel) {
		super(player);

		this.race = race;
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Race getRace() {
		return race;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(int newLevel) {
		this.newLevel = newLevel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
