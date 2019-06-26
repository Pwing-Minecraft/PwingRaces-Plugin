package net.pwing.races.events;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.race.Race;

public class RaceReclaimItemsEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private Race race;
	private Collection<ItemStack> raceItems;

	private boolean cancelled;

	public RaceReclaimItemsEvent(Player player, Race race, Collection<ItemStack> raceItems) {
		super(player);

		this.race = race;
		this.raceItems = raceItems;
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

	public Collection<ItemStack> getRaceItems() {
		return raceItems;
	}

	public void setRaceItems(Collection<ItemStack> raceItems) {
		this.raceItems = raceItems;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}