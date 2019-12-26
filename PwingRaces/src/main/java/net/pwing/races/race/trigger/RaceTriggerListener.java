package net.pwing.races.race.trigger;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceExpChangeEvent;
import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

@AllArgsConstructor
public class RaceTriggerListener implements Listener {

    private PwingRaces plugin;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "join");
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "quit");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "teleport");
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.isFlying())
            triggerManager.runTriggers(player, "fly");
        else
            triggerManager.runTriggers(player, "stop-fly");
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.isSneaking())
            triggerManager.runTriggers(player, "sneak");
        else
            triggerManager.runTriggers(player, "stop-sneak");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "take-damage");
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "take-damage " + event.getDamager().getType().name().toLowerCase());
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "damage-entity");
        triggerManager.runTriggers(player, "damage-entity " + event.getEntity().getType().name().toLowerCase());

        if (!plugin.getMythicMobsHook().isHooked())
            return;

        if (MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity())) {
            ActiveMob mythicMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(event.getEntity());

            triggerManager.runTriggers(player, "damage-mythicmob");
            triggerManager.runTriggers(player, "damage-mythicmob " + mythicMob.getType().getInternalName());
        }
    }

    @EventHandler
    public void onKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        Player player = event.getEntity().getKiller();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "kill-entity");
        triggerManager.runTriggers(player, "kill-entity " + event.getEntity().getType().name().toLowerCase());

        if (!plugin.getMythicMobsHook().isHooked())
            return;

        if (MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity())) {
            ActiveMob mythicMob = MythicMobs.inst().getAPIHelper().getMythicMobInstance(event.getEntity());

            triggerManager.runTriggers(player, "kill-mythicmob");
            triggerManager.runTriggers(player, "kill-mythicmob " + mythicMob.getType().getInternalName());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "move");
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "health-regen");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "block-break");
        triggerManager.runTriggers(player, "block-break " + event.getBlock().getType().name().toLowerCase());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "block-place");
        triggerManager.runTriggers(player, "block-place " + event.getBlock().getType().name().toLowerCase());
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "race-change");
        triggerManager.runTriggers(player, "race-change " + event.getNewRace().getName());
    }

    @EventHandler
    public void onLevelUp(RaceLevelUpEvent event) {
        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.getNewLevel() >= event.getOldLevel()) {
            triggerManager.runTriggers(player, "race-levelup");
            triggerManager.runTriggers(player, "race-levelup " + event.getNewLevel());
        } else {
            triggerManager.runTriggers(player, "race-leveldown");
            triggerManager.runTriggers(player, "race-leveldown " + event.getNewLevel());
        }
    }

    @EventHandler
    public void onBurn(EntityCombustEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "burn");
    }

    @EventHandler
    public void onRaceExpChange(RaceExpChangeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        if (event.getNewExp() >= event.getOldExp())
            triggerManager.runTriggers(player, "race-exp-gain");
        else
            triggerManager.runTriggers(player, "race-exp-lose");
    }

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "element-buy " + event.getPurchasedElement().getInternalName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "death");

        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (!(lastDamageCause instanceof EntityDamageByEntityEvent))
            return;

        Entity damager = ((EntityDamageByEntityEvent) lastDamageCause).getDamager();
        triggerManager.runTriggers(player, "killed-by " + damager.getType().name().toLowerCase());
        if (!(damager instanceof Player))
               return;

        RacePlayer targetRacePlayer = plugin.getRaceManager().getRacePlayer((Player) damager);
        if (!targetRacePlayer.getRace().isPresent())
            return;

        triggerManager.runTriggers(player, "killed-by" + targetRacePlayer.getRace().get().getName());
    }

    @EventHandler
    public void onUseInventory(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        // Anvil repairs
        if (inventory instanceof AnvilInventory) {
            if (event.getSlotType() != InventoryType.SlotType.RESULT) {
                return;
            }

            // Ensure nothing is empty
            if (inventory.getItem(0) == null || inventory.getItem(0).getType() == Material.AIR)
                return;

            if (inventory.getItem(1) == null || inventory.getItem(1).getType() == Material.AIR)
                return;

            if (inventory.getItem(2) == null || inventory.getItem(2).getType() == Material.AIR)
                return;

            RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
            triggerManager.runTriggers(player, "use-anvil");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled())
            return;

        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(event.getEnchanter(), "enchant-item");
        triggerManager.runTriggers(event.getEnchanter(), "enchant-item " + event.getItem().getType().name().toLowerCase());
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.isCancelled())
            return;

        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(event.getPlayer(), "fish");
        triggerManager.runTriggers(event.getPlayer(), "fish " + event.getCaught().getType().name().toLowerCase());

        if (event.getCaught() instanceof Item) {
            Item item = (Item) event.getCaught();
            triggerManager.runTriggers(event.getPlayer(), "fish " + item.getItemStack().getType().name().toLowerCase());
        }
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getOwner() instanceof Player))
            return;

        Player player = (Player) event.getOwner();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "tame-animal");
        triggerManager.runTriggers(player, "tame-animal " + event.getEntity().getType().name().toLowerCase());
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getBreeder() instanceof Player))
            return;

        Player player = (Player) event.getBreeder();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "breed-animal");
        triggerManager.runTriggers(player, "breed-animal " + event.getEntity().getType().name().toLowerCase());
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;

        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers((Player) event.getWhoClicked(), "craft-item");
        triggerManager.runTriggers((Player) event.getWhoClicked(), "craft-item " + event.getRecipe().getResult().getType().name().toLowerCase());
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity().getShooter();
        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(player, "launch-projectile");
        triggerManager.runTriggers(player, "launch-projectile " + event.getEntity().getType().name().toLowerCase());
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.isCancelled())
            return;

        RaceTriggerManager triggerManager = plugin.getRaceManager().getTriggerManager();
        triggerManager.runTriggers(event.getPlayer(), "consume-item");
        triggerManager.runTriggers(event.getPlayer(), "consume-item " + event.getItem().getType().name().toLowerCase());
    }
}
