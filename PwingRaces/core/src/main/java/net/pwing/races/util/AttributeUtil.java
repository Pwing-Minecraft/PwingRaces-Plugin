package net.pwing.races.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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


    public static double getDefaultAttributeValue(Player player, String attributeStr) {
        if (!isBukkitAttribute(attributeStr))
            return 0;

        String attributeName = getAttributeName(attributeStr);
        AttributeInstance attribute = player.getAttribute(Attribute.valueOf(attributeName));
        if (attribute == null) {
            return 0;
        }
        return attribute.getDefaultValue();
    }

    public static void setAttributeValue(Player player, String attributeStr, double amount) {
        if (!isBukkitAttribute(attributeStr))
            return;

        String attributeName = getAttributeName(attributeStr);
        AttributeInstance attribute = player.getAttribute(Attribute.valueOf(attributeName));
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(amount);
    }
}
