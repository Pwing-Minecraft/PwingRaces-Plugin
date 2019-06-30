package net.pwing.races.events;

import net.pwing.races.race.PwingRace;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RaceUnlockEvent extends Event implements Cancellable {

    protected static final HandlerList handlers = new HandlerList();

    private OfflinePlayer player;
    private PwingRace race;

    private boolean cancelled;

    public RaceUnlockEvent(OfflinePlayer player, PwingRace race) {
        this.player = player;
        this.race = race;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public PwingRace getRace() {
        return race;
    }

    public void setRace(PwingRace race) {
        this.race = race;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
