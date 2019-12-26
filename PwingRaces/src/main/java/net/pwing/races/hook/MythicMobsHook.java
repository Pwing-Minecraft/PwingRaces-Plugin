package net.pwing.races.hook;

import io.lumine.xikage.mythicmobs.MythicMobs;
import net.pwing.races.PwingRaces;
import org.bukkit.plugin.Plugin;

public class MythicMobsHook extends PluginHook {

    public MythicMobsHook(PwingRaces owningPlugin, String pluginName) {
        super(owningPlugin, pluginName);
    }

    @Override
    public void enableHook(PwingRaces owningPlugin, Plugin hook) {
        if (!(hook instanceof MythicMobs))
            return;
    }
}
