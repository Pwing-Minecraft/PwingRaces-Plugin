package net.pwing.races.race;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PwingRacePlayer implements RacePlayer {

	@NonNull
	private OfflinePlayer player;

	@NonNull
	private Race race;

	@NonNull
	private Map<String, RaceData> raceDataMap;

	private Map<String, EquationResult> temporaryAttributes = new HashMap<>();

	public Optional<Race> getRace() {
		return Optional.ofNullable(race);
	}
}
