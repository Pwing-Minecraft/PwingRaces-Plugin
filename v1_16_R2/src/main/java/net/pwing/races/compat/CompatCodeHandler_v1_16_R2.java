package net.pwing.races.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.pwing.races.PwingRaces;
import net.pwing.races.util.item.HeadUtil;
import net.pwing.races.util.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompatCodeHandler_v1_16_R2 extends CompatCodeHandlerDisabled {

    private PwingRaces plugin;

    public CompatCodeHandler_v1_16_R2(PwingRaces plugin) {
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
                MinecraftSessionService sessionService = ((CraftServer) Bukkit.getServer()).getServer().getMinecraftSessionService();
                sessionService.fillProfileProperties(gameProfile, true);
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = sessionService.getTextures(gameProfile, true);
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
