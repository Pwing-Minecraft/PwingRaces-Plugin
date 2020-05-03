package net.pwing.races.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.PwingRaces;

import net.pwing.races.util.RaceUtil;
import net.pwing.races.util.math.NumberUtil;
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

        if (!plugin.getRaceManager().getRacePlayer(player).getRace().isPresent())
            return "";

        Race race = plugin.getRaceManager().getRacePlayer(player).getRace().get();
        RaceData data = plugin.getRaceManager().getPlayerData(player, race);
        if (data == null)
            return "";

        switch (params) {
            case "race":
                return race.getName();
            case "race_display_name":
                return race.getDisplayName();
            case "level":
                return String.valueOf(data.getLevel());
            case "maxlevel":
                return String.valueOf(race.getMaxLevel());
            case "exp":
                return String.valueOf(data.getExperience());
            case "maxexp":
                return String.valueOf(race.getRequiredExperience(data.getLevel()));
            case "exp_until_levelup":
                return String.valueOf(race.getRequiredExperience(data.getLevel()) - data.getExperience());
            case "used_skillpoints":
                return String.valueOf(data.getUsedSkillpoints());
            case "unused_skillpoints":
                return String.valueOf(data.getUnusedSkillpoints());
        }

        if (params.startsWith("near_race_")) {
            String[] split = params.split("_");
            if (split.length < 4)
                return "";

            double radius = NumberUtil.getDouble(split[3]);
            return plugin.getRaceManager().getRaceFromName(split[2])
                    .map(value -> String.valueOf(RaceUtil.getNearbyRaceCount(player.getLocation(), value, radius)))
                    .orElse("");
        }

        return "";
    }
}
