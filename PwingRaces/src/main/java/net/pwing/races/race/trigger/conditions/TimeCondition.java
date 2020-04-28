package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

public class TimeCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        long worldTime = player.getWorld().getTime();
        char option = args[1].charAt(0);
        String timeStr = args[1].substring(1);
        if (!NumberUtil.isInteger(timeStr)) {
            return false;
        }
        long time = Long.parseLong(timeStr);
        switch (option) {
            case '=':
                return worldTime == time;
            case '>':
                return worldTime > time;
            case '<':
                return worldTime < time;
            case '%':
                return worldTime % time == 0;
        }
        return false;
    }
}
