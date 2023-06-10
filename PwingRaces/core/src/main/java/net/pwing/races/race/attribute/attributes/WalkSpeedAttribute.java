package net.pwing.races.race.attribute.attributes;

import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import org.bukkit.entity.Player;

public class WalkSpeedAttribute extends RaceAttributeEffect {

    public WalkSpeedAttribute(String name) {
        super(name);
    }

    @Override
    public void onAttributeApply(Player player, double walkSpeed) {
        player.setWalkSpeed((float) walkSpeed);
    }

    @Override
    public void onAttributeLose(Player player) {
        player.setWalkSpeed(0.2f);
    }
}
