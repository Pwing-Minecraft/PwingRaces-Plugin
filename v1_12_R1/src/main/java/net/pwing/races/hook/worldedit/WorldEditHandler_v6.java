package net.pwing.races.hook.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class WorldEditHandler_v6 implements IWorldEditHandler {

    public void pasteSchematic(Location loc, String schematic, boolean pasteAir) {
        WorldEdit we = WorldEdit.getInstance();

        LocalConfiguration config = we.getConfiguration();
        File dir = we.getWorkingDirectoryFile(config.saveDir);
        File file = new File(dir, schematic + ".schematic");

        if (!file.exists()) {
            Bukkit.getLogger().warning("Schematic " + schematic + ".schematic does not exist!");
            return;
        }

        EditSession session = we.getEditSessionFactory().getEditSession(new BukkitWorld(loc.getWorld()), -1);
        try {
            MCEditSchematicFormat.getFormat(schematic).load(file).paste(session, new Vector(loc.getX(), loc.getY(), loc.getZ()), false);
        } catch (MaxChangedBlocksException | com.sk89q.worldedit.data.DataException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
