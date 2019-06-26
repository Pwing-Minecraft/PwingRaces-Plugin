package net.pwing.races.race.ability.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.pwing.races.PwingRaces;
import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.pwing.races.race.ability.RaceAbility;

public class ShadowmeldAbility extends RaceAbility {

	private int duration;

	private String vanishMessage;
	private String unvanishMessage;

	private List<UUID> vanishedPlayers;

	public ShadowmeldAbility(PwingRaces plugin, String internalName, String configPath, YamlConfiguration config, String requirement) {
		super(plugin, internalName, configPath, config, requirement);

		duration = config.getInt(configPath + ".duration", 100);

		vanishMessage = config.getString(configPath + ".vanish-message");
		unvanishMessage = config.getString(configPath + ".unvanish-message");

		vanishedPlayers = new ArrayList<UUID>();
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
