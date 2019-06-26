package net.pwing.races.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.pwing.races.race.Race;
import net.pwing.races.race.skilltree.RaceSkilltreeElement;

public class RaceElementPurchaseEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private Race race;
	private RaceSkilltreeElement element;

	private boolean cancelled;

	public RaceElementPurchaseEvent(Player player, Race race, RaceSkilltreeElement element) {
		super(player);

		this.race = race;
		this.element = element;
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

	public RaceSkilltreeElement getPurchasedElement() {
		return element;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
