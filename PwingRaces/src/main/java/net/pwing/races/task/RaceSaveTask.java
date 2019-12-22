package net.pwing.races.task;

import net.pwing.races.api.race.RaceManager;

import org.bukkit.Bukkit;

public class RaceSaveTask implements Runnable {

	private RaceManager raceManager;

	public RaceSaveTask(RaceManager raceManager) {
		this.raceManager = raceManager;
	}

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(raceManager::savePlayer);
	}
}