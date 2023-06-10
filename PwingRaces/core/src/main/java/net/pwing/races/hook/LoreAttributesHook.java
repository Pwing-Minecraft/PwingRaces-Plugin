package net.pwing.races.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.zthana.loreattributes.LoreAttributes;

import net.pwing.races.PwingRaces;

public class LoreAttributesHook extends PluginHook {

	private boolean healthEnabled;
	private boolean armorEnabled;
	private boolean damageEnabled;

	public LoreAttributesHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof LoreAttributes))
			return;

		setupHookConfig();
		hookConfig.getConfig().addDefault("health-enabled", true);
		hookConfig.getConfig().addDefault("armor-enabled", true);
		hookConfig.getConfig().addDefault("damage-enabled", true);
		hookConfig.getConfig().options().copyDefaults(true);
		hookConfig.saveConfig();

		healthEnabled = hookConfig.getConfig().getBoolean("health-enabled", true);
		armorEnabled = hookConfig.getConfig().getBoolean("armor-enabled", true);
		damageEnabled = hookConfig.getConfig().getBoolean("damage-enabled", true);

		owningPlugin.getLogger().info("LoreAttributes found, attribute hook enabled.");
	}

	public int getHealthBonus(Player player) {
		if (!isHooked() || !healthEnabled)
			return 0;

		return LoreAttributes.loreManager.getHpBonus(player);
	}

	public int getArmorBonus(Player player) {
		if (!isHooked() || !armorEnabled)
			return 0;

		return LoreAttributes.loreManager.getArmorBonus(player);
	}

	public int getDamageBonus(Player player) {
		if (!isHooked() || !damageEnabled)
			return 0;

		return LoreAttributes.loreManager.getDamageBonus(player);
	}
}
