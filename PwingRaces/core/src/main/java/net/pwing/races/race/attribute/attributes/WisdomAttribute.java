package net.pwing.races.race.attribute.attributes;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import org.bukkit.entity.Player;

public class WisdomAttribute extends RaceAttributeEffect {

    private final PwingRaces plugin;

    public WisdomAttribute(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void onAttributeApply(Player player, double amount) {
        this.plugin.getAureliumSkillsHook().addWisdomModifier(player, amount);
    }

    @Override
    public void onAttributeLose(Player player) {
        this.plugin.getAureliumSkillsHook().removeWisdomModifier(player);
    }
}
