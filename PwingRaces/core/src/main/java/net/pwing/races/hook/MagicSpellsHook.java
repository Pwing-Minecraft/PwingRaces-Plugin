package net.pwing.races.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaChangeReason;

import net.pwing.races.PwingRaces;

public class MagicSpellsHook extends PluginHook {

	private int defMaxMana;

	public MagicSpellsHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof MagicSpells))
			return;

		setupHookConfig();

		hookConfig.getConfig().addDefault("default-max-mana", 200);
		hookConfig.getConfig().options().copyDefaults(true);
		hookConfig.saveConfig();

		defMaxMana = hookConfig.getConfig().getInt("default-max-mana", 200);

		owningPlugin.getLogger().info("MagicSpells found, mana hook enabled.");
	}

	public int getMana(Player player) {
		if (!isHooked())
			return 0;

		return MagicSpells.getManaHandler().getMana(player);
	}

	public int getMaxMana(Player player) {
		if (!isHooked())
			return 0;

		return MagicSpells.getManaHandler().getMaxMana(player);
	}

	public boolean hasMana(Player player, int mana) {
		if (!isHooked())
			return false;

		return MagicSpells.getManaHandler().hasMana(player, mana);
	}

	public boolean addMana(Player player, int mana) {
		if (!isHooked())
			return false;

		return MagicSpells.getManaHandler().setMana(player, mana, ManaChangeReason.OTHER);
	}

	public void setMaxMana(Player player, int maxMana) {
		if (!isHooked())
			return;

		MagicSpells.getManaHandler().setMaxMana(player, maxMana);
	}

	public int getDefaultMaxMana() {
		return defMaxMana;
	}
}
