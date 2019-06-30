package net.pwing.races.events;

import net.pwing.races.api.race.Race;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.pwing.races.race.PwingRace;

public class RaceReclaimSkillpointsEvent extends PlayerEvent implements Cancellable {

	protected static final HandlerList handlers = new HandlerList();

	private Race race;
	private int oldSkillpointCount;
	private int newSkillpointCount;

	private boolean cancelled;

	public RaceReclaimSkillpointsEvent(Player player, Race race, int oldSkillpointCount, int newSkillpointCount) {
		super(player);

		this.race = race;
		this.oldSkillpointCount = oldSkillpointCount;
		this.newSkillpointCount = newSkillpointCount;
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

	public int getOldSkillpointCount() {
		return oldSkillpointCount;
	}

	public int getNewSkillpointCount() {
		return newSkillpointCount;
	}

	public void setNewSkillpointCount(int newSkillpointCount) {
		this.newSkillpointCount = newSkillpointCount;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}