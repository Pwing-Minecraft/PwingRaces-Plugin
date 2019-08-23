package net.pwing.races.task;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class RaceTriggerTickTask implements Runnable {

    private PwingRaces plugin;
    private static int tick = 0;

    public RaceTriggerTickTask(PwingRaces plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        tick++;

        if (!plugin.isPluginEnabled())
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
            triggerManager.runTaskTriggers(player, "ticks " + tick, tick);

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

            // TODO: Find a way to allow multiple triggers?
            if ((time < 13000 || time > 23850) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm())
                triggerManager.runTriggers(player, "in-sunlight");

            if ((time >= 1300 && time <= 23840) && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getEyeLocation().getY() && !player.getWorld().hasStorm())
                triggerManager.runTriggers(player, "in-moonlight");

            for (BlockFace face : BlockFace.values())
                triggerManager.runTriggers(player, "block-relative " + face.name().toLowerCase() + " " + player.getLocation().getBlock().getRelative(face).getType().name().toLowerCase());

            for (World world : Bukkit.getWorlds())
                triggerManager.runTriggers(player, "in-world " + world.getName());
        }
    }
}
