package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.pwing.races.race.ability.PwingRaceAbility;

// TODO: Create paste effects and removing the schematic
public class SchematicAbility extends PwingRaceAbility {

	private String schematicName;
	private boolean pasteAir;

	public SchematicAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);

		schematicName = config.getString(configPath + ".schematic-name");
		pasteAir = config.getBoolean(configPath + ".paste-air", true);
	}

	@Override
	public boolean runAbility(Player player) {
		plugin.getWorldEditHook().pasteSchematic(player.getLocation(), schematicName, pasteAir);
		return true;
	}
}
