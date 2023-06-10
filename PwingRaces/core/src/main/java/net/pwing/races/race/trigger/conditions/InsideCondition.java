package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;

public class InsideCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        return player.getWorld().getHighestBlockYAt(player.getLocation()) > player.getEyeLocation().getY();
    }
}
