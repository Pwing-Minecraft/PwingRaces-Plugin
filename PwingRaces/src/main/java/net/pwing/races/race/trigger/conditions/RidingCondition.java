package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;
import org.bukkit.entity.Player;

public class RidingCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length > 1) {
            String vehicle = args[1];
            if (!player.isInsideVehicle()) {
                return false;
            }
            return player.getVehicle().getType().name().equalsIgnoreCase(vehicle);
        }
        return player.isInsideVehicle();
    }
}
