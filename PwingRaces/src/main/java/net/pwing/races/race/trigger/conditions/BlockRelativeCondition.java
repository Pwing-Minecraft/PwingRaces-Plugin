package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.RaceMaterial;
import net.pwing.races.util.item.SafeMaterialData;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class BlockRelativeCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 3)
            return false;

        try {
            BlockFace face = BlockFace.valueOf(args[1].toUpperCase());
            SafeMaterialData material = RaceMaterial.fromString(args[2]);
            if (material.getMaterial() == null)
                return false;

            return player.getLocation().getBlock().getRelative(face).getType() == material.getMaterial();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
