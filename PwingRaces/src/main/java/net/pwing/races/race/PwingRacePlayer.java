package net.pwing.races.race;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RacePlayer;

import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class PwingRacePlayer implements RacePlayer {

	private OfflinePlayer player;
	private Race race;
	
	private Map<String, RaceData> raceDataMap;

	public Optional<Race> getRace() {
		return Optional.ofNullable(race);
	}
}
