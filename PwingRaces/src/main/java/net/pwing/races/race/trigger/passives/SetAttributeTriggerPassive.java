package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.AttributeUtil;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.entity.Player;

import java.util.Map;

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

        if (!NumberUtil.isFloat(trigger[2])) {
            plugin.getLogger().warning("Attribute value " + trigger[2] + " for trigger " + trigger + " is not a number (float expected).");
            return;
        }

        String attribute = trigger[1];
        float value = Float.parseFloat(trigger[2]);
        if (AttributeUtil.isBukkitAttribute(attribute))
            AttributeUtil.setAttributeValue(player, attribute, value);

        Map<String, RaceAttributeEffect> attributeEffects = plugin.getRaceManager().getAttributeManager().getAttributeEffects();
        if (attributeEffects.containsKey(attribute))
            attributeEffects.get(attribute).onAttributeApply(player, value);
    }
}
