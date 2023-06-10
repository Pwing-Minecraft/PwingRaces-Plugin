package net.pwing.races.race.trigger.conditions;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.condition.RaceCondition;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class DisguisedCondition implements RaceCondition {

    private PwingRaces plugin;

    @Override
    public boolean check(Player player, String[] args) {
        if (!plugin.getLibsDisguisesHook().isHooked()) {
            return false;
        }
        if (args.length > 1) {
            String typeStr = args[1];
            try {
                return plugin.getLibsDisguisesHook().hasDisguise(player, EntityType.valueOf(typeStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return plugin.getLibsDisguisesHook().isDisguised(player);
    }
}
