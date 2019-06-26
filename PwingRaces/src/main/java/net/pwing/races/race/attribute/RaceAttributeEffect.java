package net.pwing.races.race.attribute;

import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

public abstract class RaceAttributeEffect {

    protected PwingRaces plugin;
    protected String name;

    public RaceAttributeEffect(PwingRaces plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract void onAttributeApply(Player player, double amount);
    public abstract void onAttributeLose(Player player);

    public String getName() {
        return name;
    }
}
