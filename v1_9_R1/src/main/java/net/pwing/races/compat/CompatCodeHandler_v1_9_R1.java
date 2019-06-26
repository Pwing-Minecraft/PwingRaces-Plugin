package net.pwing.races.compat;

import net.pwing.races.PwingRaces;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_9_R1.attribute.CraftAttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class CompatCodeHandler_v1_9_R1 extends CompatCodeHandlerDisabled {

	private PwingRaces plugin;

	public CompatCodeHandler_v1_9_R1(PwingRaces plugin) {
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

    private net.minecraft.server.v1_9_R1.AttributeInstance convert(AttributeInstance bukkit) {
        CraftAttributeInstance instance = (CraftAttributeInstance) bukkit;
        try {
            Field field = instance.getClass().getDeclaredField("handle");
            field.setAccessible(true);
            return (net.minecraft.server.v1_9_R1.AttributeInstance) field.get(instance);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
	}
}
