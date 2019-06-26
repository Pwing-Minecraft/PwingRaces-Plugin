package net.pwing.races.utilities;


import net.pwing.races.PwingRaces;
import org.bukkit.entity.Player;

public class AttributeUtil {

    public static boolean isBukkitAttribute(String name) {
        return PwingRaces.getInstance().getCompatCodeHandler().isBukkitAttribute(name);
    }

    public static String getAttributeName(String name) {
        return PwingRaces.getInstance().getCompatCodeHandler().getAttributeName(name);
    }

    public static double getAttributeValue(Player player, String attribute) {
        return PwingRaces.getInstance().getCompatCodeHandler().getAttributeValue(player, attribute);
    }

    public static double getDefaultAttributeValue(Player player, String attribute) {
        return PwingRaces.getInstance().getCompatCodeHandler().getDefaultAttributeValue(player, attribute);
    }

    public static void setAttributeValue(Player player, String attribute, double amount) {
        PwingRaces.getInstance().getCompatCodeHandler().setAttributeValue(player, attribute, amount);
    }
}
