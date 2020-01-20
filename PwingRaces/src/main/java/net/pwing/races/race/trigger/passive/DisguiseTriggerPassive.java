package net.pwing.races.race.trigger.passive;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DisguiseTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public DisguiseTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");
        if (split.length < 2)
            return;

        EntityType type = null;
        try {
            type = EntityType.valueOf(split[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            return;
        }

        plugin.getLibsDisguisesHook().disguiseEntity(player, type);
    }
}
