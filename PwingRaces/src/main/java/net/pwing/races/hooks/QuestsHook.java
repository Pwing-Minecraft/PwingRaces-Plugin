package net.pwing.races.hooks;

import org.bukkit.plugin.Plugin;

import me.blackvein.quests.Quests;
import net.pwing.races.PwingRaces;
import net.pwing.races.hooks.quests.RaceExperienceRequirement;
import net.pwing.races.hooks.quests.RaceExperienceReward;
import net.pwing.races.hooks.quests.RaceLevelRequirement;
import net.pwing.races.hooks.quests.RaceLevelReward;
import net.pwing.races.hooks.quests.RaceRequirement;
import net.pwing.races.hooks.quests.RaceReward;

public class QuestsHook extends PluginHook {

	private Quests quests;

	public QuestsHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		if (!(hook instanceof Quests))
			return;

		this.quests = (Quests) hook;
		owningPlugin.getLogger().info("Quests found, questing integration enabled.");

		quests.getCustomRewards().add(new RaceExperienceReward(owningPlugin.getRaceManager()));
		quests.getCustomRewards().add(new RaceLevelReward(owningPlugin.getRaceManager()));
		quests.getCustomRewards().add(new RaceReward(owningPlugin.getRaceManager()));

		quests.getCustomRequirements().add(new RaceExperienceRequirement(owningPlugin.getRaceManager()));
		quests.getCustomRequirements().add(new RaceLevelRequirement(owningPlugin.getRaceManager()));
		quests.getCustomRequirements().add(new RaceRequirement(owningPlugin.getRaceManager()));
	}
}
