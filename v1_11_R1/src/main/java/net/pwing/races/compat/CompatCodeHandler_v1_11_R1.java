package net.pwing.races.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import net.pwing.races.PwingRaces;
import net.pwing.races.util.AttributeUtil;
import net.pwing.races.util.item.HeadUtil;
import net.pwing.races.util.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompatCodeHandler_v1_11_R1 extends CompatCodeHandlerDisabled {

    private PwingRaces plugin;

    public CompatCodeHandler_v1_11_R1(PwingRaces plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public void setUnbreakable(ItemStack item, boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
    }

    @Override
    public void setColor(ItemStack item, Color color) {
        super.setColor(item, color);

        if (item.getItemMeta() instanceof PotionMeta) {
            PotionMeta im = (PotionMeta) item.getItemMeta();
            im.setColor(color);
            item.setItemMeta(im);
        }
    }

    @Override
    public double getDefaultAttributeValue(Player player, String attribute) {
        if (!AttributeUtil.isBukkitAttribute(attribute))
            return 0;

        String attributeName = AttributeUtil.getAttributeName(attribute);
        return player.getAttribute(Attribute.valueOf(attributeName)).getDefaultValue();
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
                MinecraftSessionService sessionService = ((CraftServer) Bukkit.getServer()).getServer().az();
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
