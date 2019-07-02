package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.pwing.races.race.ability.PwingRaceAbility;

public class AllowFlightAbility extends PwingRaceAbility {

	private int duration;

	public AllowFlightAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);

		duration = config.getInt(configPath + ".duration", 100);
	}

	@Override
	public boolean runAbility(Player player) {
		boolean toggled = player.getAllowFlight();
		if (player.getGameMode() == GameMode.CREATIVE)
			return false;

		player.setAllowFlight(!toggled);
		new BukkitRunnable() {

			@Override
			public void run() {
				player.setAllowFlight(toggled);
			}
		}.runTaskLater(plugin, duration);

		return true;
	}
}
