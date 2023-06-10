package net.pwing.races.race.attribute.attributes;

import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import org.bukkit.entity.Player;

public class FlySpeedAttribute extends RaceAttributeEffect {

    public FlySpeedAttribute(String name) {
        super(name);
    }

    @Override
    public void onAttributeApply(Player player, double flySpeed) {
        player.setFlySpeed((float) flySpeed);
    }

    @Override
    public void onAttributeLose(Player player) {
        player.setFlySpeed(0.2f);
    }
}
