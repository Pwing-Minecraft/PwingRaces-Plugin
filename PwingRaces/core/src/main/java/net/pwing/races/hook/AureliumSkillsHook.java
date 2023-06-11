package net.pwing.races.hook;

import com.archyx.aureliumskills.AureliumSkills;
import net.pwing.races.PwingRaces;
import net.pwing.races.hook.aureliumskills.AureliumSkillsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AureliumSkillsHook extends PluginHook {
    private AureliumSkillsHandler aureliumSkillsHandler;

    public AureliumSkillsHook(PwingRaces owningPlugin, String pluginName) {
        super(owningPlugin, pluginName);
    }

    @Override
    public void enableHook(PwingRaces owningPlugin, Plugin hook) {
        if (!(hook instanceof AureliumSkills)) {
            return;
        }

        owningPlugin.getLogger().info("AureliumSkills found, wisdom hook enabled.");
        this.aureliumSkillsHandler = new AureliumSkillsHandler();
    }

    public void addWisdomModifier(Player player, double widsom) {
        if (!isHooked()) {
            return;
        }

        this.aureliumSkillsHandler.addWisdomModifier(player, widsom);
    }

    public void removeWisdomModifier(Player player) {
        if (!isHooked()) {
            return;
        }

        this.aureliumSkillsHandler.removeWisdomModifier(player);
    }
}
