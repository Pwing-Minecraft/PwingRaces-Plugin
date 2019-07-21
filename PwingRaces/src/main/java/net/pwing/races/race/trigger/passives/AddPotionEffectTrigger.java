package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pwing.races.utilities.NumberUtil;

public class AddPotionEffectTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public AddPotionEffectTrigger(PwingRaces plugin, String trigger) {
        super(trigger);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");

        PotionEffectType effectType = PotionEffectType.getByName(split[1]);
        if (effectType == null) {
            plugin.getLogger().warning("PotionEffectType " + effectType + " for trigger " + trigger + " is invalid.");
            return;
        }

        int duration = 0;
        int amplifier = 0;

        if (NumberUtil.isInteger(split[2]))
            duration = Integer.parseInt(split[2]) * 20;

        if (NumberUtil.isInteger(split[3]))
            amplifier = Integer.parseInt(split[3]) - 1;

        boolean clear = true;
        if (split.length > 4) {
            clear = Boolean.parseBoolean(split[4]);
        }

        if (clear) {
            player.removePotionEffect(effectType);
        }

        player.addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, false));
    }
}
