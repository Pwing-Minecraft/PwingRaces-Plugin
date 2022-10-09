package net.pwing.races.race.attribute;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class RaceAttributeListener implements Listener {

    private PwingRaces plugin;

    public RaceAttributeListener(PwingRaces plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(event.getPlayer());
        racePlayer.getTemporaryAttributes().clear();

        plugin.getRaceManager().getAttributeManager().removeAttributeBonuses(event.getPlayer());
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        if (event.isCancelled())
            return;

        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(event.getPlayer());
        racePlayer.getTemporaryAttributes().clear();

        plugin.getRaceManager().getAttributeManager().removeAttributeBonuses(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer()), 20);
    }

    @EventHandler
    public void onRaceReclaimSkillpoints(RaceReclaimSkillpointsEvent event) {
        if (event.isCancelled())
            return;

        this.plugin.getRaceManager().getAttributeManager().removeAttributeBonuses(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer()), 20);
    }

    @EventHandler
    public void onRaceExpChange(RaceLevelUpEvent event) {
        plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer());
    }

    @EventHandler
    public void onRaceElementPurchase(RaceElementPurchaseEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer()), 20);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.getRaceManager().getAttributeManager().applyAttributeBonuses(event.getPlayer());
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        RaceManager raceManager = plugin.getRaceManager();
        Player player = (Player) event.getEntity();
        if (!raceManager.isRacesEnabledInWorld(player.getWorld()))
            return;

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        event.setAmount(event.getAmount() + raceManager.getAttributeManager().getAttributeBonus(player, "health-regen"));
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getDamager();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        RaceManager raceManager = plugin.getRaceManager();
        Player player = (Player) arrow.getShooter();
        if (!raceManager.isRacesEnabledInWorld(player.getWorld()))
            return;

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        double bonus = raceManager.getAttributeManager().getAttributeBonus(player, "arrow-damage");
        arrow.setDamage(arrow.getDamage() + bonus);
    }

    @EventHandler
    public void onMeleeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        RaceManager raceManager = plugin.getRaceManager();
        Player player = (Player) event.getDamager();
        if (!raceManager.isRacesEnabledInWorld(player.getWorld()))
            return;

        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;

        double bonus = raceManager.getAttributeManager().getAttributeBonus(player, "melee-damage");

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand != null && hand.getType().name().contains("SWORD")) {
            bonus += raceManager.getAttributeManager().getAttributeBonus(player, "swords-damage");
        }

        if (hand != null && hand.getType().name().contains("_AXE")) {
            bonus += raceManager.getAttributeManager().getAttributeBonus(player, "axes-damage");
        }

        event.setDamage(event.getDamage() + bonus);
    }
}
