package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.entity.Player;

public class HungerCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        double playerHunger = player.getFoodLevel();
        char option = args[1].charAt(0);
        String hungerStr = args[1].substring(1);
        if (!NumberUtil.isInteger(hungerStr)) {
            return false;
        }
        int hunger = Integer.parseInt(hungerStr);
        switch (option) {
            case '=':
                return playerHunger == hunger;
            case '>':
                return playerHunger > hunger;
            case '<':
                return playerHunger < hunger;
            case '%':
                return (playerHunger % hunger) == 0;
        }
        return false;
    }
}
