package net.pwing.races.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.pwing.races.race.Race;

public class RaceExpChangeEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private Race race;

	private int oldExp;
	private int newExp;

	private boolean cancelled;

	public RaceExpChangeEvent(Player player, Race race, int oldExp, int newExp) {
		super(player);

		this.race = race;
		this.oldExp = oldExp;
		this.newExp = newExp;
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

	public int getOldExp() {
		return oldExp;
	}

	public int getNewExp() {
		return newExp;
	}

	public void setNewExp(int newExp) {
		this.newExp = newExp;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
