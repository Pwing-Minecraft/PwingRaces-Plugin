package net.pwing.races.race.ability;

import lombok.AllArgsConstructor;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.config.RaceConfigurationManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class RaceAbilityListener implements Listener {

    private PwingRaces plugin;

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        // MagicSpells bug here (?), won't let you leftclick the air
        // if (event.isCancelled())
        //	return;

        Player player = event.getPlayer();
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        RaceConfigurationManager configManager = plugin.getConfigManager();
        for (RaceAbility ability : raceManager.getAbilityManager().getApplicableAbilities(player, race)) {
            for (ItemStack stack : ability.getAbilityItems()) {
                if (!ability.canRun(player, stack))
                    continue;

                if (configManager.isUseProjectileEvent() && isProjectileLauncher(stack.getType().name(), configManager.getProjectileLaunchers()))
                    continue;

                if (raceManager.getAbilityManager().runAbility(player, ability)) {
                    if (ability.isDefaultActionCancelled())
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onShootBow(ProjectileLaunchEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity().getShooter();
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        RaceConfigurationManager configManager = plugin.getConfigManager();
        if (!configManager.isUseProjectileEvent() && isProjectile(event.getEntityType().name(), configManager.getProjectileTypes()))
            return;

        for (RaceAbility ability : raceManager.getAbilityManager().getApplicableAbilities(player, race)) {
            for (ItemStack stack : ability.getAbilityItems()) {
                if (!ability.canRun(player, stack))
                    continue;

                if (raceManager.getAbilityManager().runAbility(player, ability)) {
                    if (ability.isDefaultActionCancelled())
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        for (RaceAbility ability : raceManager.getAbilityManager().getApplicableAbilities(player, race)) {
            for (ItemStack stack : ability.getLeftClickAbilityItems()) {
                if (!ability.canRun(player, stack))
                    continue;

                if (raceManager.getAbilityManager().runAbility(player, ability)) {
                    if (ability.isDefaultActionCancelled())
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (!racePlayer.getRace().isPresent())
            return;

        Race race = racePlayer.getRace().get();
        for (RaceAbility ability : raceManager.getAbilityManager().getApplicableAbilities(player, race)) {
            for (ItemStack stack : ability.getConsumeAbilityItems()) {
                if (!ability.canRun(player, stack))
                    continue;

                if (raceManager.getAbilityManager().runAbility(player, ability)) {
                    if (ability.isDefaultActionCancelled())
                        event.setCancelled(true);

                    break;
                }
            }
        }
    }

    private boolean isProjectile(String proj, List<String> projectiles) {
        for (String str : projectiles) {
            if (str.replace("_", "").equalsIgnoreCase(proj.replace("_", "")))
                return true;
        }

        return false;
    }

    private boolean isProjectileLauncher(String item, List<String> projectileLaunchers) {
        for (String str : projectileLaunchers) {
            if (str.replace("_", "").equalsIgnoreCase(item.replace("_", "")))
                return true;
        }

        return false;
    }
}
