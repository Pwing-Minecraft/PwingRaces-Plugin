package net.pwing.races.util;

import net.pwing.races.PwingRaces;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class AttributeUtil {

    public static boolean isBukkitAttribute(String name) {
        try {
            Attribute.valueOf(getAttributeName(name));
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }

    public static String getAttributeName(String name) {
        name = name.toUpperCase().replace("-", "_");

        String bukkitAttribute = name;
        if (!bukkitAttribute.startsWith("GENERIC_"))
            bukkitAttribute = "GENERIC_" + name;

        try {
            Attribute.valueOf(bukkitAttribute);
            return bukkitAttribute;
        } catch (Exception ex) {/* do nothing */}

        return name;
    }

    public static double getAttributeValue(Player player, String attribute) {
        if (!isBukkitAttribute(attribute))
            return 0;

        String attributeName = getAttributeName(attribute);
        return player.getAttribute(Attribute.valueOf(attributeName)).getBaseValue();
    }

    public static double getDefaultAttributeValue(Player player, String attribute) {
        return PwingRaces.getInstance().getCompatCodeHandler().getDefaultAttributeValue(player, attribute);
    }

    public static void setAttributeValue(Player player, String attribute, double amount) {
        if (!isBukkitAttribute(attribute))
            return;

        String attributeName = getAttributeName(attribute);
        player.getAttribute(Attribute.valueOf(attributeName)).setBaseValue(amount);
    }
}
