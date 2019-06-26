package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.pwing.races.race.ability.RaceAbility;
import net.pwing.races.utilities.RaceSound;

public class QuickshotAbility extends RaceAbility {

	private boolean spendArrows;

	private int arrows;
	private double damage;
	private int spread;
	private int speed;
	private int delay;

	private int yOffset;

	public QuickshotAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);

		spendArrows = config.getBoolean(configPath + ".spend-arrows", true);

		arrows = config.getInt(configPath + ".arrows", 1);
		damage = config.getDouble(configPath + ".damage", 1);
		spread = config.getInt(configPath + ".spread", 5);
		speed = config.getInt(configPath + ".speed", 2);
		delay = config.getInt(configPath + ".delay", 10);

		yOffset = config.getInt(configPath + ".y-offset", 2);
	}

	@Override
	public boolean runAbility(Player player) {
		if (spendArrows && !player.getInventory().contains(Material.ARROW, arrows))
			return false;

		new BukkitRunnable() {
			int i = 1;

			@Override
			public void run() {
				Location loc = player.getLocation().clone().add(0D, yOffset, 0D);

				Arrow arrow = loc.getWorld().spawnArrow(loc, player.getLocation().getDirection(), speed, spread);
				arrow.setShooter(player);
				arrow.setMetadata("PwingRacesSource", new FixedMetadataValue(plugin, "Quickshot" + internalName));
				//arrow.setPickupStatus(PickupStatus.ALLOWED);
				plugin.getCompatCodeHandler().setDamage(arrow, damage);
				if (!spendArrows)
					plugin.getCompatCodeHandler().setPickupStatus(arrow, "CREATIVE_ONLY");

				player.playSound(loc, RaceSound.ENTITY_ARROW_SHOOT.parseSound(), 1f, (float) (1.5 - (Math.random() * 1f)));

				if (spendArrows)
					player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));

				if (i == arrows)
					this.cancel();

				i++;
			}
		}.runTaskTimer(plugin, delay, delay);
		return true;
	}
}
