package net.pwing.races.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.PwingRaces;

import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

	private PwingRaces plugin;

	public PlaceholderAPIHook(PwingRaces plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getAuthor() {
		return "Redned";
	}

	@Override
	public String getIdentifier() {
		return "PwingRaces";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String params) {
		if (player == null)
			return "";

		Race race = plugin.getRaceManager().getRacePlayer(player).getActiveRace();
		if (race == null) {
			return "";
		}

		RaceData data = plugin.getRaceManager().getPlayerData(player, race);
		switch (params) {
			case "race":
				return race.getName();
			case "level":
				return String.valueOf(data.getLevel());
			case "maxlevel":
				return String.valueOf(race.getMaxLevel());
			case "exp":
				return String.valueOf(data.getExperience());
			case "exp_until_levelup":
				return String.valueOf(race.getRequiredExperience(data.getLevel()) - data.getExperience());
			case "used_skillpoints":
				return String.valueOf(data.getUsedSkillpoints());
			case "unused_skillpoints":
				return String.valueOf(data.getUnusedSkillpoints());
		}
		return null;
	}
}
