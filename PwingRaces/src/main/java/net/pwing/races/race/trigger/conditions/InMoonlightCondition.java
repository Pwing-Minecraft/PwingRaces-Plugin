package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;

public class InMoonlightCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        long time = player.getWorld().getTime();
        return (time >= 1300 && time <= 23840) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm();
    }
}
