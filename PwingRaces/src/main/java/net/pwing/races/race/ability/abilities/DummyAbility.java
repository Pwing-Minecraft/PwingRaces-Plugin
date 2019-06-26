package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.pwing.races.race.ability.RaceAbility;

// This class serves as a placeholder or for people to just use the trigger passives
public class DummyAbility extends RaceAbility {

	public DummyAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);
	}

	@Override
	public boolean runAbility(Player player) {
		return true;
	}
}
