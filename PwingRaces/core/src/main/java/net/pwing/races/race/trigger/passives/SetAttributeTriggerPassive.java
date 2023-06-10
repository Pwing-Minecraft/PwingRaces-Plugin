package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.api.util.math.EquationResult;
import net.pwing.races.util.AttributeUtil;
import net.pwing.races.util.math.EquationUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SetAttributeTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public SetAttributeTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        if (trigger.length < 3)
            return;

        String attribute = trigger[1];
        EquationResult result = EquationUtil.getEquationResult(player, trigger[2]);

        long duration = -1;
        if (trigger.length >= 4)
            duration = Long.parseLong(trigger[3]);

        if (AttributeUtil.isBukkitAttribute(attribute))
            AttributeUtil.setAttributeValue(player, attribute, result.getResult());

        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        racePlayer.getTemporaryAttributes().put(attribute, result);
        if (duration > -1) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                racePlayer.getTemporaryAttributes().remove(attribute);
                plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(player);
            }, duration);
        }
        plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(player);
    }
}
