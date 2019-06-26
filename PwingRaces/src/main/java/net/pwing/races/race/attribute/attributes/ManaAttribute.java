package net.pwing.races.race.attribute.attributes;

import net.pwing.races.PwingRaces;
import net.pwing.races.hooks.MagicSpellsHook;
import net.pwing.races.race.attribute.RaceAttributeEffect;
import org.bukkit.entity.Player;

public class ManaAttribute extends RaceAttributeEffect {

    public ManaAttribute(PwingRaces plugin, String name) {
        super(plugin, name);
    }

    @Override
    public void onAttributeApply(Player player, double amount) {
        plugin.getMagicSpellsHook().setMaxMana(player, (int) amount);
    }

    @Override
    public void onAttributeLose(Player player) {
        MagicSpellsHook msHook = plugin.getMagicSpellsHook();
        msHook.setMaxMana(player, msHook.getDefaultMaxMana());
    }
}
