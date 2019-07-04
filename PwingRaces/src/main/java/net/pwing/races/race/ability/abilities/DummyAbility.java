package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

// This class serves as a placeholder or for people to just use the trigger passives
public class DummyAbility extends PwingRaceAbility {

    public DummyAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);
    }

    @Override
    public boolean runAbility(Player player) {
        return true;
    }
}
