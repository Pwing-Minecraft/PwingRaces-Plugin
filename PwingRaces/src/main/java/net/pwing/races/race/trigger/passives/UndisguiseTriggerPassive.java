package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.Player;

public class UndisguiseTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public UndisguiseTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        plugin.getLibsDisguisesHook().undisguiseEntity(player);
    }
}
