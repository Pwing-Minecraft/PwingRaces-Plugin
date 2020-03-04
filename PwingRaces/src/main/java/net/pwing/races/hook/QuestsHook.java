package net.pwing.races.hook;

import me.blackvein.quests.Quests;

import net.pwing.races.PwingRaces;
import net.pwing.races.hook.quests.RaceExperienceRequirement;
import net.pwing.races.hook.quests.RaceExperienceReward;
import net.pwing.races.hook.quests.RaceLevelRequirement;
import net.pwing.races.hook.quests.RaceLevelReward;
import net.pwing.races.hook.quests.RaceRequirement;
import net.pwing.races.hook.quests.RaceReward;

import org.bukkit.plugin.Plugin;

public class QuestsHook extends PluginHook {

	public QuestsHook(PwingRaces owningPlugin, String pluginName) {
		super(owningPlugin, pluginName);
	}

	@Override
	public void enableHook(PwingRaces owningPlugin, Plugin hook) {
		try {
			if (!Class.forName("me.blackvein.quests.Quests").isAssignableFrom(hook.getClass()))
				return;
		} catch (Throwable ex) {
			return;
		}

		Quests quests = (Quests) hook;
		owningPlugin.getLogger().info("Quests found, questing integration enabled.");

		quests.getCustomRewards().add(new RaceExperienceReward(owningPlugin.getRaceManager()));
		quests.getCustomRewards().add(new RaceLevelReward(owningPlugin.getRaceManager()));
		quests.getCustomRewards().add(new RaceReward(owningPlugin.getRaceManager()));

		quests.getCustomRequirements().add(new RaceExperienceRequirement(owningPlugin.getRaceManager()));
		quests.getCustomRequirements().add(new RaceLevelRequirement(owningPlugin.getRaceManager()));
		quests.getCustomRequirements().add(new RaceRequirement(owningPlugin.getRaceManager()));
	}
}
