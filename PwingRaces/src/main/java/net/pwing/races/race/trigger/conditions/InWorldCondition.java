package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.Player;

public class InWorldCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2)
            return false;

        return player.getWorld().getName().equals(args[1]);
    }
}
