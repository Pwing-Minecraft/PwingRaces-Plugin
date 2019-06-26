package net.pwing.races.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.pwing.races.race.RaceManager;

public class RaceSaveTask implements Runnable {

	private RaceManager raceManager;

	public RaceSaveTask(RaceManager raceManager) {
		this.raceManager = raceManager;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers())
			raceManager.savePlayer(player);
	}
}