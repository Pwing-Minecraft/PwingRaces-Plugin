package net.pwing.races.race;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.util.math.EquationResult;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class PwingRacePlayer implements RacePlayer {

	private OfflinePlayer player;
	private Race race;
	private Map<String, RaceData> raceDataMap;

	public PwingRacePlayer(OfflinePlayer player, Race race, Map<String, RaceData> raceDataMap) {
		this.player = player;
		this.race = race;
		this.raceDataMap = raceDataMap;
	}

	private Map<String, EquationResult> temporaryAttributes = new HashMap<>();

	public Optional<Race> getRace() {
		return Optional.ofNullable(race);
	}

	@Override
	public void setRace(Race race) {
		this.race = race;
	}
}
