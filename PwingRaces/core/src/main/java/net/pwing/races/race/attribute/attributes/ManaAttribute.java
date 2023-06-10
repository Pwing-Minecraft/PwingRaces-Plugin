package net.pwing.races.race.attribute.attributes;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import net.pwing.races.hook.MagicSpellsHook;

import org.bukkit.entity.Player;

public class ManaAttribute extends RaceAttributeEffect {

    private PwingRaces plugin;

    public ManaAttribute(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
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
