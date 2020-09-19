package net.pwing.races.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.pwing.races.PwingRaces;
import net.pwing.races.util.VersionUtil;
import net.pwing.races.util.item.HeadUtil;
import net.pwing.races.util.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompatCodeHandlerModern extends CompatCodeHandlerDisabled {

    private static final Class<?> CRAFT_SERVER;
    private static final Class<?> DEDICATED_SERVER;

    private static final Method SERVER_METHOD;
    private static final Method SESSION_SERVICE_METHOD;

    private static final MinecraftSessionService SESSION_SERVICE;

    static {
        try {
            CRAFT_SERVER = Class.forName("org.bukkit.craftbukkit." + VersionUtil.getNMSPackage() + ".CraftServer");
            DEDICATED_SERVER = Class.forName("net.minecraft.server." + VersionUtil.getNMSPackage() + ".DedicatedServer");
            SERVER_METHOD = CRAFT_SERVER.getMethod("getServer");
            SESSION_SERVICE_METHOD = DEDICATED_SERVER.getMethod("getMinecraftSessionService");

            SESSION_SERVICE = (MinecraftSessionService) SESSION_SERVICE_METHOD.invoke(SERVER_METHOD.invoke(Bukkit.getServer()));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to initialize modern compatibility code handler for " + VersionUtil.getNMSPackage(), e);
        }
    }

    private PwingRaces plugin;

    public CompatCodeHandlerModern(PwingRaces plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public int getDamage(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable)
            return ((Damageable) item.getItemMeta()).getDamage();

        return 0;
    }

    @Override
    public void setDamage(ItemStack item, int damage) {
        ItemMeta meta = item.getItemMeta();

        if (item.getItemMeta() instanceof Damageable) {
            Damageable damageMeta = (Damageable) meta;
            damageMeta.setDamage(damage);
            item.setItemMeta(meta);
        }
    }

    @Override
    public void setCustomModelData(ItemStack item, int data) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(data);
        item.setItemMeta(meta);
    }

    @Override
    public Enchantment getEnchantment(String name) {
        return EnchantmentWrapper.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
    }

    @Override
    public double getDamage(Arrow arrow) {
        return arrow.getDamage();
    }

    @Override
    public void setDamage(Arrow arrow, double damage) {
        arrow.setDamage(damage);
    }

    @Override
    public CompletableFuture<String> getHeadURL(String player) {
        String url = null;
        CompletableFuture<String> future = new CompletableFuture<>();
        if (HeadUtil.getCachedHeads().containsKey(player)) {
            return CompletableFuture.completedFuture(HeadUtil.getCachedHeads().get(player));
        } else {
            try {
                GameProfile gameProfile = new GameProfile(UUIDFetcher.getUUIDOf(player), player);
                SESSION_SERVICE.fillProfileProperties(gameProfile, true);
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = SESSION_SERVICE.getTextures(gameProfile, true);
                MinecraftProfileTexture texture = textures.get(MinecraftProfileTexture.Type.SKIN);
                if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    url = texture.getUrl();
                    HeadUtil.getCachedHeads().put(player, url);
                }
            } catch (Exception ex) {
                return CompletableFuture.completedFuture(null);
            }
        }

        future.complete(url);
        return future;
    }
}
