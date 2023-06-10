package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class InBiomeCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2)
            return false;

        Biome biome;
        try {
            biome = Biome.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()) == biome;
    }
}
