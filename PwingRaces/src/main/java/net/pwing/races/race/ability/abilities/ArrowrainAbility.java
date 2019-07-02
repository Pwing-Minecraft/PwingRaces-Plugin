package net.pwing.races.race.ability.abilities;

import java.util.Set;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ArrowrainAbility extends PwingRaceAbility {

	private boolean spendArrows;

	private int arrows;
	private double damage;
	private int spread;
	private int speed;

	private int yOffset;
	//private boolean useDamagePlugins;

	public ArrowrainAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);

		spendArrows = config.getBoolean(configPath + ".spend-arrows", true);

		arrows = config.getInt(configPath + ".arrows", 5);
		damage = config.getDouble(configPath + ".damage", 1);
		spread = config.getInt(configPath + ".spread", 15);
		speed = config.getInt(configPath + ".speed", 2);

		yOffset = config.getInt(configPath + ".y-offset", 3);
		//useDamagePlugins = config.getBoolean("use-damage-plugins", false);
	}

	@Override
	public boolean runAbility(Player player) {
		Block block = player.getTargetBlock((Set<Material>) null, 10);

		if (block == null || block.getType() == Material.AIR)
			return false;

		Location loc = player.getLocation().clone().add(0D, yOffset, 0D);
		if (spendArrows && !player.getInventory().contains(Material.ARROW, arrows))
			return false;

		for (int i = 0; i < arrows; i++) {
			Arrow arrow = loc.getWorld().spawnArrow(loc, block.getLocation().toVector().subtract(loc.toVector()).normalize(), speed, spread);
			arrow.setShooter(player);
			arrow.setMetadata("PwingRacesSource", new FixedMetadataValue(plugin, "Arrowrain" + internalName));
			//arrow.setPickupStatus(PickupStatus.DISALLOWED);
			plugin.getCompatCodeHandler().setDamage(arrow, damage);
			if (!spendArrows)
				plugin.getCompatCodeHandler().setPickupStatus(arrow, "CREATIVE_ONLY");
		}

		if (spendArrows)
			player.getInventory().removeItem(new ItemStack(Material.ARROW, arrows));

		return true;
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow))
			return;

		if (event.getEntity().hasMetadata("Arrowrain" + internalName)) {
			Arrow arrow = (Arrow) event.getEntity();
			arrow.remove();

		}
	}
}
