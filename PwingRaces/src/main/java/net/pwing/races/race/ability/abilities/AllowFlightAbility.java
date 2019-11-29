package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AllowFlightAbility extends PwingRaceAbility {

    private int duration;

    public AllowFlightAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        duration = config.getInt(configPath + ".duration", 100);
    }

    @Override
    public boolean runAbility(Player player) {
        boolean toggled = player.getAllowFlight();
        if (player.getGameMode() == GameMode.CREATIVE)
            return false;

        player.setAllowFlight(!toggled);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setAllowFlight(toggled), duration);
        return true;
    }
}
