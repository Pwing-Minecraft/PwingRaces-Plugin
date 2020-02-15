package net.pwing.races.race.trigger.triggers;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class InRegionTrigger implements RaceCondition {

    private PwingRaces plugin;

    public InRegionTrigger(PwingRaces plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Task(), 20, 20);
    }

    @Override
    public boolean check(Player player, String[] args) {
        if (!plugin.getWorldGuardHook().isHooked())
            return false;

        for (String region : plugin.getWorldGuardHook().getRegions(player.getLocation())) {
            if (args[0].equals(region)) {
                return true;
            }
        }

        return false;
    }

    public class Task implements Runnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!plugin.getWorldGuardHook().getRegions(player.getLocation()).isEmpty()) {
                    plugin.getWorldGuardHook().getRegions(player.getLocation()).forEach(region ->
                            plugin.getRaceManager().getTriggerManager().runTriggers(player, "in-region " + region));
                }

                if (plugin.getWorldGuardHook().getLastRegionsCache().containsKey(player.getUniqueId())) {
                    List<String> leftRegions = plugin.getWorldGuardHook().getLastRegionsCache().get(player.getUniqueId());
                    if (!leftRegions.isEmpty()) {
                        leftRegions.removeAll(plugin.getWorldGuardHook().getRegions(player.getLocation()));
                        leftRegions.forEach(region -> plugin.getRaceManager().getTriggerManager().runTriggers(player, "leave-region " + region));
                    }
                }

                plugin.getWorldGuardHook().getLastRegionsCache().put(player.getUniqueId(), plugin.getWorldGuardHook().getRegions(player.getLocation()));
            }
        }
    }
}
