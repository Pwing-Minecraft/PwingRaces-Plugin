package net.pwing.races.util;

import org.bukkit.Bukkit;

public class VersionUtil {

    public static String getNMSPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
