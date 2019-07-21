package net.pwing.races.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.pwing.races.PwingRaces;
import net.pwing.races.utilities.HeadUtil;
import net.pwing.races.utilities.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.attribute.CraftAttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class CompatCodeHandler_v1_10_R1 extends CompatCodeHandlerDisabled {

	private PwingRaces plugin;

	public CompatCodeHandler_v1_10_R1(PwingRaces plugin) {
		super(plugin);

		this.plugin = plugin;
	}

	@Override
	public double getMaxHealth(Player player) {
		return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
	}

	@Override
	public void setMaxHealth(Player player, double maxHealth) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
	}

	@Override
	public ItemStack getItemInMainHand(Player player) {
		return player.getInventory().getItemInMainHand();
	}

	@Override
	public boolean isBukkitAttribute(String attribute) {
		try {
			Attribute.valueOf(attribute);
			return true;
		} catch (IllegalArgumentException ex) {/* do nothing */}

		return false;
	}

	@Override
	public String getAttributeName(String str) {
		str = str.toUpperCase().replace("-", "_");

		String bukkitAttribute = str;
		if (!bukkitAttribute.startsWith("GENERIC_"))
			bukkitAttribute = "GENERIC_" + str;

		try {
			Attribute.valueOf(bukkitAttribute);
			return bukkitAttribute;
		} catch (Exception ex) {/* do nothing */}

		return str;
	}

	@Override
	public double getAttributeValue(Player player, String attribute) {
		if (!isBukkitAttribute(attribute))
			return 0;

		String attributeName = getAttributeName(attribute);
		return player.getAttribute(Attribute.valueOf(attributeName)).getBaseValue();
	}

	@Override
	public double getDefaultAttributeValue(Player player, String attribute) {
		if (!isBukkitAttribute(attribute))
			return 0;

		String attributeName = getAttributeName(attribute);
		return convert(player.getAttribute(Attribute.valueOf(attributeName))).getAttribute().b();
	}

	@Override
	public void setAttributeValue(Player player, String attribute, double amount) {
		if (!isBukkitAttribute(attribute))
			return;

		String attributeName = getAttributeName(attribute);
		player.getAttribute(Attribute.valueOf(attributeName)).setBaseValue(amount);
	}

	private net.minecraft.server.v1_10_R1.AttributeInstance convert(AttributeInstance bukkit) {
		CraftAttributeInstance instance = (CraftAttributeInstance) bukkit;
		try {
			Field field = instance.getClass().getDeclaredField("handle");
			field.setAccessible(true);
			return (net.minecraft.server.v1_10_R1.AttributeInstance) field.get(instance);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getHeadURL(String player) {
		String url = null;
		if (HeadUtil.getCachedHeads().containsKey(player)) {
			return HeadUtil.getCachedHeads().get(player);
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
				return null;
			}
		}

		return url;
	}
}
