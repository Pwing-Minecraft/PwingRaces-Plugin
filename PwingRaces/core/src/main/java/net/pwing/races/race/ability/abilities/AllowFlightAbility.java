package net.pwing.races.race.ability.abilities;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.PwingRacesAPI;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AllowFlightAbility extends PwingRaceAbility {

    private int duration;
    private Multimap<String, RaceTriggerPassive> finishedPassives = HashMultimap.create();

    public AllowFlightAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        duration = config.getInt(configPath + ".duration", 100);
        for (String passive : config.getStringList(configPath + ".finished-passives")) {
            String passiveName = passive.split(" ")[0];
            if (PwingRacesAPI.getTriggerManager().getTriggerPassives().containsKey(passiveName)) {
                this.finishedPassives.put(passive, PwingRacesAPI.getTriggerManager().getTriggerPassives().get(passiveName));
            }
        }
    }

    @Override
    public boolean runAbility(Player player) {
        boolean toggled = player.getAllowFlight();
        if (player.getGameMode() == GameMode.CREATIVE)
            return false;

        player.setAllowFlight(!toggled);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.setAllowFlight(toggled);
            finishedPassives.keySet().forEach(fullPassive -> finishedPassives.get(fullPassive).forEach(passive -> passive.runPassive(player, fullPassive)));
        }, duration);
        return true;
    }
}
