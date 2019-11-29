package net.pwing.races.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.pwing.races.PwingRaces;
import net.pwing.races.utilities.AttributeUtil;
import net.pwing.races.utilities.HeadUtil;
import net.pwing.races.utilities.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.attribute.CraftAttributeInstance;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompatCodeHandler_v1_9_R2 extends CompatCodeHandlerDisabled {

	private PwingRaces plugin;

	public CompatCodeHandler_v1_9_R2(PwingRaces plugin) {
		super(plugin);

		this.plugin = plugin;
	}

	@Override
	public double getDefaultAttributeValue(Player player, String attribute) {
		if (!AttributeUtil.isBukkitAttribute(attribute))
			return 0;

		String attributeName = AttributeUtil.getAttributeName(attribute);
		return convert(player.getAttribute(Attribute.valueOf(attributeName))).getAttribute().b();
	}

	private net.minecraft.server.v1_9_R2.AttributeInstance convert(AttributeInstance bukkit) {
		CraftAttributeInstance instance = (CraftAttributeInstance) bukkit;
		try {
			Field field = instance.getClass().getDeclaredField("handle");
			field.setAccessible(true);
			return (net.minecraft.server.v1_9_R2.AttributeInstance) field.get(instance);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
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
				MinecraftSessionService sessionService = ((CraftServer) Bukkit.getServer()).getServer().ay();
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
