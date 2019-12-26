package net.pwing.races.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static Location fromString(String loc) {
        if (loc == null || loc.isEmpty())
            return null;

        String[] split = loc.split(";");
        if (split.length < 4)
            return null;

        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);

        if (split.length == 6) {
            return new Location(world, x, y, z, Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        } else {
            return new Location(world, x, y, z);
        }
    }

    public static String toString(Location loc) {
        return loc.getWorld().toString() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
}
