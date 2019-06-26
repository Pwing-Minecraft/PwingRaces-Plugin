package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.NumberUtil;

public class AddPotionEffectTrigger extends RaceTriggerPassive {

	public AddPotionEffectTrigger(PwingRaces plugin, String trigger) {
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

		int duration = 0;
		int amplifier = 0;

		if (NumberUtil.isInteger(split[2]))
			duration = Integer.parseInt(split[2]) * 20;

		if (NumberUtil.isInteger(split[3]))
			amplifier = Integer.parseInt(split[3]) - 1;

		player.addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, false));
	}
}
