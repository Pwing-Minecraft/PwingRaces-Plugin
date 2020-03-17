package net.pwing.races.race.trigger.triggers.holder;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * This class holds all the triggers when it comes to environment
 * tasks. This is solely a trigger class and mainly is here to
 * prevent potential performance problems on lower-end machines.
 *
 * Conditions specifically for this can be found in the
 * conditions package.
 */
@AllArgsConstructor
public class EnvironmentTriggerHolder implements Runnable {

    private RaceTriggerManager triggerManager;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            long time = player.getWorld().getTime();
            if (time < 13000 || time > 23850) {
                triggerManager.runTriggers(player, "day");
            } else {
                triggerManager.runTriggers(player, "night");
            }

            if (player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY())
                triggerManager.runTriggers(player, "outside");
            else
                triggerManager.runTriggers(player, "inside");

            if ((time < 13000 || time > 23850) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm())
                triggerManager.runTriggers(player, "in-sunlight");

            if ((time >= 13000 && time <= 23840) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm())
                triggerManager.runTriggers(player, "in-moonlight");

            for (BlockFace face : BlockFace.values())
                triggerManager.runTriggers(player, "block-relative " + face.name().toLowerCase() + " " + player.getLocation().getBlock().getRelative(face).getType().name().toLowerCase());

            for (World world : Bukkit.getWorlds())
                triggerManager.runTriggers(player, "in-world " + world.getName());

        }
    }
}
