package net.pwing.races.race.ability.abilities;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.PwingRacesAPI;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.race.ability.PwingRaceAbility;
import net.pwing.races.util.MessageUtil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShadowmeldAbility extends PwingRaceAbility {

    private int duration;

    private String vanishMessage;
    private String unvanishMessage;

    private List<UUID> vanishedPlayers = new ArrayList<>();
    private Multimap<String, RaceTriggerPassive> finishedPassives = HashMultimap.create();

    public ShadowmeldAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(plugin, internalName, configPath, config, requirement);

        duration = config.getInt(configPath + ".duration", 100);

        vanishMessage = config.getString(configPath + ".vanish-message");
        unvanishMessage = config.getString(configPath + ".unvanish-message");
        for (String passive : config.getStringList(configPath + ".finished-passives")) {
            String passiveName = passive.split(" ")[0];
            if (PwingRacesAPI.getTriggerManager().getTriggerPassives().containsKey(passiveName)) {
                this.finishedPassives.put(passive, PwingRacesAPI.getTriggerManager().getTriggerPassives().get(passiveName));
            }
        }
    }

    @Override
    public boolean runAbility(Player player) {
        String vanishMessage = MessageUtil.getPlaceholderMessage(player, this.vanishMessage);
        String unvanishMessage = MessageUtil.getPlaceholderMessage(player, this.unvanishMessage);
        player.sendMessage(vanishMessage);

        vanishedPlayers.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(unvanishMessage);
            vanishedPlayers.remove(player.getUniqueId());
            finishedPassives.keySet().forEach(fullPassive -> finishedPassives.get(fullPassive).forEach(passive -> passive.runPassive(player, fullPassive)));
        }, duration);

        return true;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled())
            return;

        if (!(event.getTarget() instanceof Player))
            return;

        Player player = (Player) event.getTarget();
        if (!vanishedPlayers.contains(player.getUniqueId()))
            return;

        event.setCancelled(true);
    }
}
