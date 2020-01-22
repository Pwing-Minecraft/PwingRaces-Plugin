package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;

public class InSunlightCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        long time = player.getWorld().getTime();
        return (time < 13000 || time > 23850) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm();
    }
}
