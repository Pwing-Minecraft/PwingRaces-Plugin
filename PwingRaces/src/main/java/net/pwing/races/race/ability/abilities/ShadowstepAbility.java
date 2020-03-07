package net.pwing.races.race.ability.abilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.ability.PwingRaceAbility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShadowstepAbility extends PwingRaceAbility {

    private List<UUID> shadowstepping = new ArrayList<>();

    private int range;
    private double damage;
    private long removeDamageTime;

    private boolean damagePlayers;
    private boolean checkPlugins;
    private boolean damageEntitiesInPath;

    private List<EntityType> ignoredEntities;

    public ShadowstepAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        range = config.getInt(configPath + ".range", 10);
        damage = config.getDouble(configPath + ".damage", 5);
        removeDamageTime = config.getLong(configPath + ".remove-damage-time", 100);

        damagePlayers = config.getBoolean(configPath + ".damage-players", false);
        checkPlugins = config.getBoolean(configPath + ".check-plugins", true);
        damageEntitiesInPath = config.getBoolean(configPath + ".damage-entities-in-path", false);

        ignoredEntities = config.getStringList(configPath + ".ignored-entites").stream()
                .map(str -> str.toUpperCase().replace(" ", "_"))
                .map(EntityType::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public boolean runAbility(Player player) {
        Entity target = getTarget(player);
        if (target == null)
            return false;

        if (!damagePlayers && target instanceof Player)
            return false;

        if (ignoredEntities.contains(target.getType()))
            return false;

        double x, z;
        float yaw = target.getLocation().getYaw() + 90;

        if (yaw < 0)
            yaw += 360;

        x = Math.cos(Math.toRadians(yaw));
        z = Math.sin(Math.toRadians(yaw));

        Location loc = target.getLocation();
        Location newLoc = new Location(target.getWorld(), loc.getX() - x, loc.getY(), loc.getZ() - z, loc.getYaw(), loc.getPitch());
        player.teleport(newLoc);
        shadowstepping.add(player.getUniqueId());

        List<Entity> entities = getEntitiesInLineOfSight(player);
        if (!entities.isEmpty() && damageEntitiesInPath) {
            for (Entity entity : entities) {
                if (entity.equals(target))
                    continue;

                if (entity.equals(player))
                    continue;

                if (ignoredEntities.contains(entity.getType()))
                    continue;

                if (entity instanceof Player && !damagePlayers)
                    continue;

                if (entity instanceof Player && checkPlugins && !plugin.getWorldGuardHook().hasFlag("PVP", entity.getLocation()))
                    continue;

                if (!(entity instanceof LivingEntity))
                    continue;

                EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
                plugin.getServer().getPluginManager().callEvent(damageEvent);

                if (!damageEvent.isCancelled()) {
                    LivingEntity livingEnt = (LivingEntity) entity;
                    livingEnt.damage(damageEvent.getDamage());
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            shadowstepping.remove(player.getUniqueId());
        }, removeDamageTime);

        return true;
    }

    private Entity getTarget(Player player) {
        BlockIterator iterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), player.getEyeLocation().getDirection(), 0, range);
        Entity target = null;
        while (iterator.hasNext()) {
            Block item = iterator.next();
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                int acc = 2;
                for (int x = -acc; x < acc; x++)
                    for (int y = -acc; y < acc; y++)
                        for (int z = -acc; z < acc; z++)
                            if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item)) {
                                return entity;
                }
            }
        }

        return target;
    }

    private List<Entity> getEntitiesInLineOfSight(Player player) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (player.hasLineOfSight(entity))
                entities.add(entity);
        }

        return entities;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
            return;

        if (ignoredEntities.contains(event.getEntityType()))
            return;

        if (event.getEntity() instanceof Player && !damagePlayers)
            return;

        if (!shadowstepping.contains(event.getDamager().getUniqueId()))
            return;

        event.setDamage(event.getDamage() + damage);
        shadowstepping.remove(event.getDamager().getUniqueId());
    }
}
