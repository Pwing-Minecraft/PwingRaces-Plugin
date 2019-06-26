package net.pwing.races.hooks.worldedit;

import org.bukkit.Location;

public interface IWorldEditHandler {

    void pasteSchematic(Location loc, String schematic, boolean pasteAir);
}
