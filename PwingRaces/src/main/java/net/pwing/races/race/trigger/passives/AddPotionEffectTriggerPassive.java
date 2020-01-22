package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pwing.races.util.math.NumberUtil;

public class AddPotionEffectTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public AddPotionEffectTriggerPassive(PwingRaces plugin, String trigger) {
        super(trigger);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        PotionEffectType effectType = PotionEffectType.getByName(trigger[1]);
        if (effectType == null) {
            plugin.getLogger().warning("PotionEffectType " + effectType + " for trigger " + trigger + " is invalid.");
            return;
        }

        int duration = 0;
        int amplifier = 0;

        if (NumberUtil.isInteger(trigger[2]))
            duration = Integer.parseInt(trigger[2]) * 20;

        if (NumberUtil.isInteger(trigger[3]))
            amplifier = Integer.parseInt(trigger[3]) - 1;

        boolean clear = true;
        if (trigger.length > 4) {
            clear = Boolean.parseBoolean(trigger[4]);
        }

        if (clear) {
            player.removePotionEffect(effectType);
        }

        player.addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, false));
    }
}
