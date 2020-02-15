package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import org.bukkit.entity.Player;

public class MoonPhaseCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2)
            return false;

        long time = player.getWorld().getFullTime();
        int phase = (int) (time / 24000) % 8;
        switch (phase) {
            case 0:
                return args[1].equalsIgnoreCase("full");
            case 1:
                return args[1].equalsIgnoreCase("waning_gibbous");
            case 2:
                return args[1].equalsIgnoreCase("last_quarter");
            case 3:
                return args[1].equalsIgnoreCase("waning_crescent");
            case 4:
                return args[1].equalsIgnoreCase("new");
            case 5:
                return args[1].equalsIgnoreCase("waxing_crescent");
            case 6:
                return args[1].equalsIgnoreCase("first_quarter");
            case 7:
                return args[1].equalsIgnoreCase("waxing_gibbous");
            default:
                return false;
        }
    }
}
