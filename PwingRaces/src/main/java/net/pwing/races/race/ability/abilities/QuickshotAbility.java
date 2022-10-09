package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickshotAbility extends PwingRaceAbility {

    private boolean spendArrows;
    private boolean spendArrowsOnInfinityEnchant;

    private int arrows;
    private double damage;
    private int spread;
    private int speed;
    private double durabilityModifier;

    private double yOffset;

    public QuickshotAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        spendArrows = config.getBoolean(configPath + ".spend-arrows", true);
        spendArrowsOnInfinityEnchant = config.getBoolean(configPath + ".spend-arrows-on-infinity-enchant", false);

        arrows = config.getInt(configPath + ".arrows", 1);
        damage = config.getDouble(configPath + ".damage", 1);
        spread = config.getInt(configPath + ".spread", 5);
        speed = config.getInt(configPath + ".speed", 2);
        durabilityModifier = config.getDouble(configPath + ".durability-modifier", 0.10);

        yOffset = config.getDouble(configPath + ".y-offset", 1.6);

        // Do not cancel by default
        if (!config.contains(configPath + ".cancel-default-action")) {
            cancelDefaultAction = false;
        }
    }

    @Override
    public boolean runAbility(Player player) {
        for (int i = 0; i < arrows; i++) {
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (player.getItemInUse() == null || player.getItemInUse().getType() != Material.BOW) {
                    task.cancel();
                    return;
                }

                boolean spendArrows = this.spendArrows;
                if (!spendArrowsOnInfinityEnchant && player.getItemInUse().getEnchantments().containsKey(Enchantment.ARROW_INFINITE)) {
                    spendArrows = false;
                }

                if (spendArrows && !player.getInventory().contains(Material.ARROW)) {
                    task.cancel();
                    return;
                }

                Location loc = player.getLocation().clone().add(0D, yOffset, 0D);

                Arrow arrow = loc.getWorld().spawnArrow(loc, player.getLocation().getDirection(), speed, spread);
                arrow.setShooter(player);
                arrow.setMetadata("PwingRacesSource", new FixedMetadataValue(plugin, "Quickshot" + internalName));
                //arrow.setPickupStatus(PickupStatus.ALLOWED);
                arrow.setDamage(damage);
                if (!spendArrows) {
                    arrow.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
                }

                player.playSound(loc, Sound.ENTITY_ARROW_SHOOT, 1f, (float) (1.5 - (Math.random() * 1f)));

                if (spendArrows) {
                    player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                }

                ItemStack item = player.getItemInUse();
                ItemMeta meta = item.getItemMeta();
                if (meta.isUnbreakable()) {
                    return;
                }

                boolean damage = ThreadLocalRandom.current().nextDouble(0, 1) < durabilityModifier;
                if (damage) {
                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(damageable.getDamage() + 1);
                        if (damageable.getDamage() >= item.getType().getMaxDurability()) {
                            player.playEffect(EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
                            item.setAmount(0);
                            task.cancel();
                            return;
                        }
                    }
                    item.setItemMeta(meta);
                }
            }, (int) (cooldown * 20) + (int) ((((double) i) / arrows) * ((int) (cooldown * 20))), (int) (cooldown * 20) + (int) ((((double) i) / arrows) * ((int) (cooldown * 20))));
        }

        return true;
    }
}
