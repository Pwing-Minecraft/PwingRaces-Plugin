package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;
import net.pwing.races.utilities.RaceMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ThrowWeaponAbility extends PwingRaceAbility {

    private float hitVelocity;
    private float speed;
    private double damage;
    private int maxDuration;

    private boolean useOffhandItem;
    private boolean takeItemOnThrow;
    private boolean damagePlayers;
    private boolean checkPlugins;

    private List<EntityType> ignoredEntities;

    private int yOffset;

    public ThrowWeaponAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        hitVelocity = (float) config.getDouble(configPath + ".hit-velocity", 1.5);
        speed = (float) config.getDouble(configPath + ".speed", 2);
        damage = config.getDouble(configPath + ".damage", 5);
        maxDuration = config.getInt(configPath + ".max-duration", 100);

        useOffhandItem = config.getBoolean(configPath + ".use-offhand-item", false);
        takeItemOnThrow = config.getBoolean(configPath + ".take-item-on-throw", false);
        damagePlayers = config.getBoolean(configPath + ".damage-players", false);
        checkPlugins = config.getBoolean(configPath + ".check-plugins", true);

        ignoredEntities = new ArrayList<EntityType>();
        List<String> entityList = config.getStringList(configPath + ".ignored-entites");
        entityList.forEach(entityStr -> {
            ignoredEntities.add(EntityType.valueOf(entityStr.toUpperCase().replace(" ", "_")));
        });

        yOffset = config.getInt(configPath + ".y-offset", 0);
    }

    @Override
    public boolean runAbility(Player player) {
        if (takeItemOnThrow) {
            ItemStack item = plugin.getCompatCodeHandler().getItemInMainHand(player);
            // Don't worry about support here, because if this is set and the user
            // is using 1.8, they will get an error in console regardless
            if (useOffhandItem)
                item = player.getInventory().getItemInOffHand();

            if (!player.getInventory().contains(item))
                return false;
        }

        throwWeapon(player);
        return true;
    }

    private void throwWeapon(Player player) {
        Location loc = player.getLocation().clone();
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc.add(0D, yOffset, 0D), EntityType.ARMOR_STAND);
        stand.setArms(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setRightArmPose(new EulerAngle(Math.toRadians(350.0D), Math.toRadians(loc.getPitch() * -1.0D), Math.toRadians(90.0D)));
        stand.setVisible(false);

        ItemStack item;
        if (useOffhandItem)
            item = player.getInventory().getItemInOffHand().clone();
        else
            item = player.getInventory().getItemInMainHand().clone();

        stand.getEquipment().setItemInMainHand(item);

        Vector right = new Vector(-0.8D, 1.45D, 0.0D);
        Vector front = new Vector(0.0D, 0.0D, 1.0D);
        Location weaponStart = rotateAroundYAxis(right, loc.getYaw()).toLocation(player.getWorld()).add(loc).add(rotateAroundYAxis(rotateAroundXAxis(front, loc.getPitch()), loc.getYaw()));

        final ItemStack finalItem = item.clone();
        if (takeItemOnThrow)
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                Vector vec = weaponStart.clone().getDirection();
                Location frontLoc = stand.getLocation().clone().add(0D, yOffset + 1.5D, 0D).add(vec);
                Collection<Entity> entities = stand.getLocation().clone().add(0D, 1D, 0D).getWorld().getNearbyEntities(frontLoc, 1D, 1D, 1D);
                if (entities != null && !entities.isEmpty()) {
                    for (Entity entity : entities) {
                        // if (i < hitDelay)
                        //     break;

                        if (ignoredEntities.contains(entity.getType()))
                            continue;

                        if (entity.equals(player))
                            continue;

                        if (entity instanceof Player && !damagePlayers)
                            continue;

                        if (entity instanceof Player && checkPlugins && !plugin.getWorldGuardHook().hasFlag("PVP", entity.getLocation()))
                            continue;

                        if (!(entity instanceof LivingEntity))
                            continue;

                        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.PROJECTILE, damage);
                        plugin.getServer().getPluginManager().callEvent(damageEvent);

                        if (!damageEvent.isCancelled()) {
                            LivingEntity livingEnt = (LivingEntity) entity;
                            livingEnt.damage(damageEvent.getDamage());
                            livingEnt.setVelocity(player.getLocation().clone().getDirection().multiply(hitVelocity));

                        }

                        stand.remove();
                        if (takeItemOnThrow)
                            player.getInventory().addItem(item);

                        player.updateInventory();
                        cancel();
                        return;
                    }
                }

                Material weaponMat = stand.getLocation().clone().add(0D, 1D, 0D).getBlock().getType();
                if (weaponMat != RaceMaterial.AIR.parseMaterial() && weaponMat != RaceMaterial.VOID_AIR.parseMaterial() && weaponMat != RaceMaterial.CAVE_AIR.parseMaterial()) {
                    stand.remove();
                    if (takeItemOnThrow)
                        player.getInventory().addItem(finalItem);

                    player.updateInventory();
                    cancel();
                    return;
                }

                vec = stand.getLocation().getDirection().multiply(speed).normalize();
                frontLoc = stand.getLocation().add(vec);
                stand.teleport(frontLoc);

                i += 1;
                if (i >= maxDuration) {
                    stand.remove();
                    if (takeItemOnThrow)
                        player.getInventory().addItem(finalItem);

                    player.updateInventory();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    public Vector rotateAroundXAxis(Vector v, double angle) {
        angle = Math.toRadians(angle);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundYAxis(Vector vec, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vec.getX() * cos + vec.getZ() * sin;
        double z = vec.getX() * -sin + vec.getZ() * cos;
        return vec.setX(x).setZ(z);
    }
}
