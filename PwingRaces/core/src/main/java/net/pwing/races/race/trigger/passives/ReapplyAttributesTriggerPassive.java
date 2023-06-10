package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.Player;

public class ReapplyAttributesTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public ReapplyAttributesTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (!racePlayer.hasRace())
            return;

        if (trigger.length > 1 && Boolean.parseBoolean(trigger[0])) {
            racePlayer.getTemporaryAttributes().clear();
        }

        plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(player);
    }
}
