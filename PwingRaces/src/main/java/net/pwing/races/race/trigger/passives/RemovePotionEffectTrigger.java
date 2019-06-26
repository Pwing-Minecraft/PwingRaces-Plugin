package net.pwing.races.race.trigger.passives;


import net.pwing.races.PwingRaces;
import net.pwing.races.race.trigger.RaceTriggerPassive;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RemovePotionEffectTrigger  extends RaceTriggerPassive {

    public RemovePotionEffectTrigger(PwingRaces plugin, String trigger) {
        super(plugin, trigger);
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");
        PotionEffectType effectType = PotionEffectType.getByName(split[1]);
        if (effectType == null) {
            plugin.getLogger().warning("PotionEffectType " + effectType + " for trigger " + trigger + " is invalid.");
            return;
        }

        player.removePotionEffect(effectType);
    }
}