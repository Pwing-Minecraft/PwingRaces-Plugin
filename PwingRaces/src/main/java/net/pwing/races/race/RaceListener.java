package net.pwing.races.race;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.events.RaceRespawnEvent;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pwing.races.api.events.RaceChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RaceListener implements Listener {

    private PwingRaces plugin;

    public RaceListener(PwingRaces plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RaceManager raceManager = plugin.getRaceManager();
        raceManager.registerPlayer(player);
        if (!raceManager.setupPlayer(player)) {
            plugin.getLogger().severe("Could not setup data for player " + player.getName() + "... Retrying in 5 seconds");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!raceManager.setupPlayer(player)) {
                    plugin.getLogger().severe("Could not setup data for player " + player.getName() + " after retry. Please contact " + plugin.getDescription().getAuthors().get(0));
                } else {
                    runSetupTask(player);
                }
            }, 100);
        } else {
            runSetupTask(player);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getRaceManager().savePlayer(event.getPlayer());
        plugin.getLibsDisguisesHook().undisguiseEntity(event.getPlayer());
        plugin.getRaceManager().getRacePlayerMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            plugin.getLogger().warning(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("invalid-player", "%prefix% &cThat player does not exist!")));
            return;
        }

        Race race = racePlayer.getActiveRace();
        if (race == null)
            return;

        if (!race.hasSpawnLocation())
            return;

        RaceRespawnEvent respawnEvent = new RaceRespawnEvent(player, race, race.getSpawnLocation());
        Bukkit.getPluginManager().callEvent(respawnEvent);
        if (respawnEvent.isCancelled())
            return;

        event.setRespawnLocation(race.getSpawnLocation());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(event.getPlayer().getWorld()))
            plugin.getLibsDisguisesHook().undisguiseEntity(event.getPlayer());
    }

    @EventHandler
    public void onRaceChange(RaceChangeEvent event) {
        if (!plugin.getConfigManager().isGiveItemsOnRaceChangeEnabled())
            return;

        event.getNewRace().getRaceItems().values().forEach(item -> event.getPlayer().getInventory().addItem(item));
        plugin.getLibsDisguisesHook().undisguiseEntity(event.getPlayer());
    }

    protected void runSetupTask(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
            if (racePlayer == null) {
                plugin.getLogger().severe("Could not find or create race player data for player " + player.getName() + "!");
                return;
            }

            Race race = racePlayer.getActiveRace();
            if (race == null)
                return;

            RaceData raceData = plugin.getRaceManager().getRacePlayer(player).getRaceData(race);
            if (plugin.getConfigManager().sendSkillpointMessageOnJoin()) {
                player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("skillpoint-amount-message", "%prefix% &aYou have %skillpoints% unused skillpoints.").replace("%skillpoints%", String.valueOf(raceData.getUnusedSkillpoints()))));
            }
        }, 20);
    }
}
