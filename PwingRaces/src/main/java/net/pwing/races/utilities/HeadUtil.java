package net.pwing.races.utilities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.pwing.races.PwingRaces;
import net.pwing.races.builder.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeadUtil {

    private static Map<String, String> cachedHeads = new HashMap<>();

    public static ItemStack getPlayerHead(ItemStack item, String player) {
        ItemBuilder head = new ItemBuilder(item);

        String url = getSkullURL(player);
        if (url == null || url.isEmpty()) {
            head.setOwner(player);
            return head.toItemStack();
        }

        ItemStack headStack = head.toItemStack();
        if (!(headStack.getItemMeta() instanceof SkullMeta)) {
            head.setOwner(player);
            return head.toItemStack();
        }

        SkullMeta headMeta = (SkullMeta) headStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        headStack.setItemMeta(headMeta);
        return headStack;
    }

    public static String getSkullURL(String player) {
        if (cachedHeads.containsKey(player)) {
            return cachedHeads.get(player);
        }

        try {
            return PwingRaces.getInstance().getCompatCodeHandler().getHeadURL(player);
        } catch (Exception ex) {
            PwingRaces.getInstance().getLogger().warning("Client has sent too many requests to Mojang's server, using builtin head system.");
            return null;
        }
    }

    public static Map<String, String> getCachedHeads() {
        return cachedHeads;
    }
}
