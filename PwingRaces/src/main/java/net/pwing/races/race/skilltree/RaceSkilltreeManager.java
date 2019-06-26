package net.pwing.races.race.skilltree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class RaceSkilltreeManager {

	private List<RaceSkilltree> skilltrees;

	public RaceSkilltreeManager(File dir) {
		skilltrees = new ArrayList<RaceSkilltree>();
		initSkilltrees(dir);
	}

	public void initSkilltrees(File dir) {
		if (dir == null || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(".yml"))
				continue;

			initSkilltree(file.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
		}
	}

	public void initSkilltree(String regName, YamlConfiguration config) {
		skilltrees.add(new RaceSkilltree(regName, config));
	}

	public RaceSkilltree getSkilltreeFromName(String name) {
		for (RaceSkilltree skilltree : skilltrees) {
			if (skilltree.getName().equalsIgnoreCase(name) || skilltree.getRegName().equalsIgnoreCase(name))
				return skilltree;
		}

		return null;
	}

	public List<RaceSkilltree> getSkilltrees() {
		return skilltrees;
	}
}
