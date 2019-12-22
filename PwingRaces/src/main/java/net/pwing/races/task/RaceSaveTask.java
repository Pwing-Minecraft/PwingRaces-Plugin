package net.pwing.races.task;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.RaceManager;

import org.bukkit.Bukkit;

@AllArgsConstructor
public class RaceSaveTask implements Runnable {

	private RaceManager raceManager;

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(raceManager::savePlayer);
	}
}