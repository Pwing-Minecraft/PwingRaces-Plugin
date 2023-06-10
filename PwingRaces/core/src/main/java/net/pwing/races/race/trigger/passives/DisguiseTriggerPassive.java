package net.pwing.races.race.trigger.passives;

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
    public void runTriggerPassive(Player player, String[] trigger) {
        if (trigger.length < 2)
            return;

        EntityType type;
        try {
            type = EntityType.valueOf(trigger[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            return;
        }

        plugin.getLibsDisguisesHook().disguiseEntity(player, type);
    }
}
