package net.pwing.races.events;

import net.pwing.races.api.race.Race;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class RaceRespawnEvent extends PlayerEvent implements Cancellable {

    protected static final HandlerList handlers = new HandlerList();

    private Race race;
    private Location spawnLocation;

    private boolean cancelled;

    public RaceRespawnEvent(Player player, Race race, Location spawnLocation) {
        super(player);
        this.race = race;
        this.spawnLocation = spawnLocation;
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

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
