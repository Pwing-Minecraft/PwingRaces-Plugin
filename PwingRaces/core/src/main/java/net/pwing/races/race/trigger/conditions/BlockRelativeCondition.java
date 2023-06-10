package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class BlockRelativeCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 3)
            return false;

        try {
            BlockFace face = BlockFace.valueOf(args[1].toUpperCase());
            BlockData blockData;
            try {
                blockData = Bukkit.createBlockData(args[2]);
            } catch (IllegalArgumentException ex) {
                return false;
            }

            return player.getLocation().getBlock().getRelative(face).getBlockData().equals(blockData);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
