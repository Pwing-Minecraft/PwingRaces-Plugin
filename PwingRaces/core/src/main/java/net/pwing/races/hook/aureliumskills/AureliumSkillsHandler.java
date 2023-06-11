package net.pwing.races.hook.aureliumskills;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.stats.Stats;
import org.bukkit.entity.Player;

public class AureliumSkillsHandler {

    public void addWisdomModifier(Player player, double widsom) {
        AureliumAPI.addStatModifier(player.getPlayer(), "pwingraces-wisdom-modifier", Stats.WISDOM, widsom);
    }

    public void removeWisdomModifier(Player player) {
        AureliumAPI.removeStatModifier(player.getPlayer(), "pwingraces-wisdom-modifier");
    }
}
