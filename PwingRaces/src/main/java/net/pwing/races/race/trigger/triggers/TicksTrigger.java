package net.pwing.races.race.trigger.triggers;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TicksTrigger implements Listener, RaceCondition {

    private static int tick = 0;

    public TicksTrigger(PwingRaces plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Task(plugin), 1, 1);
    }

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2)
            return true; // all ticks lol

        return tick % Integer.parseInt(args[1]) == 0;
    }

    static class Task implements Runnable {

        private PwingRaces plugin;

        public Task(PwingRaces plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getRaceManager().getTriggerManager().runTaskTriggers(player, "ticks " + tick, tick);
            }
            tick++;
        }
    }
}
