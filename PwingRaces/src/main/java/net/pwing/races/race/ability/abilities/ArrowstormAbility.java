package net.pwing.races.race.ability.abilities;

import java.util.Random;
import java.util.Set;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowstormAbility extends PwingRaceAbility {

    private boolean spendArrows;

    private int arrows;
    private double damage;
    private int spread;
    private int velocitySpread;
    private int delay;

    private int yOffset;

    private boolean playEffects;
    private int effectDuration;

    public ArrowstormAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        spendArrows = config.getBoolean(configPath + ".spend-arrows", true);

        arrows = config.getInt(configPath + ".arrows", 20);
        damage = config.getDouble(configPath + ".damage", 1);
        spread = config.getInt(configPath + ".spread", 5);
        velocitySpread = config.getInt(configPath + ".velocity-spread", 5);
        delay = config.getInt(configPath + ".delay", 2);

        yOffset = config.getInt(configPath + ".y-offset", 15);

        playEffects = config.getBoolean(configPath + ".play-passives", true);
        effectDuration = config.getInt(configPath + ".effect-duration", 200);
    }

    @Override
    public boolean runAbility(Player player) {
        Block block = player.getTargetBlock(null, 10);

        if (block == null || block.getType() == Material.AIR)
            return false;

        Random random = new Random();
        if (spendArrows && !player.getInventory().contains(Material.ARROW, arrows))
            return false;

        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                Location loc = block.getLocation().clone().add((random.nextInt(spread) * 2) - spread, yOffset, random.nextInt((spread * 2) - spread));

                Arrow arrow = loc.getWorld().spawnArrow(loc, block.getLocation().toVector().subtract(loc.toVector()).normalize(), 1, velocitySpread);
                arrow.setShooter(player);
                arrow.setMetadata("PwingRacesSource", new FixedMetadataValue(plugin, "Arrowstorm" + internalName));
                // arrow.setPickupStatus(PickupStatus.ALLOWED);
                plugin.getCompatCodeHandler().setDamage(arrow, damage);
                if (!spendArrows)
                    arrow.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);

                if (i == arrows)
                    this.cancel();

                i++;
            }
        }.runTaskTimer(plugin, delay, delay);

        if (playEffects)
            playEffects(player);

        return true;
    }

    public void playEffects(Player player) {
        if (!playEffects)
            return;

        player.setPlayerWeather(WeatherType.DOWNFALL);

        new BukkitRunnable() {
            int i = 1;

            @Override
            public void run() {
                Random random = new Random();
                Location loc = player.getLocation().clone().add(random.nextInt(5), random.nextInt(5), random.nextInt(5));

                player.getWorld().strikeLightningEffect(loc);

                if (i == effectDuration / 20) {
                    player.setPlayerWeather(WeatherType.CLEAR);
                    this.cancel();
                }

                i++;
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}
