package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.entity.Player;

public class HealthCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        double playerHealth = player.getHealth();
        char option = args[1].charAt(0);
        String healthStr = args[1].substring(1);
        if (!NumberUtil.isDouble(healthStr)) {
            return false;
        }
        double health = Double.parseDouble(healthStr);
        switch (option) {
            case '=':
                return playerHealth == health;
            case '>':
                return playerHealth > health;
            case '<':
                return playerHealth < health;
            case '%':
                return (playerHealth % health) == 0;
        }
        return false;
    }
}
