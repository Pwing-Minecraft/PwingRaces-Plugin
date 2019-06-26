package net.pwing.races.compat;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import net.pwing.races.PwingRaces;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

public class CompatCodeHandler_v1_8_R1 extends CompatCodeHandlerDisabled {

	private PwingRaces plugin;

	public CompatCodeHandler_v1_8_R1(PwingRaces plugin) {
		super(plugin);

		this.plugin = plugin;
	}
}
