package net.pwing.races.race.leveling;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.events.RaceExpChangeEvent;
import net.pwing.races.api.events.RaceLevelUpEvent;
import net.pwing.races.utilities.MessageUtil;
import net.pwing.races.utilities.RaceSound;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RaceLevelManager {

    private PwingRaces plugin;

    public RaceLevelManager(PwingRaces plugin) {
        this.plugin = plugin;
    }

    public void setExperience(Player player, Race race, int amount) {
        RaceData data = plugin.getRaceManager().getPlayerData(player, race);
        RaceExpChangeEvent event = new RaceExpChangeEvent(player, race, data.getExperience(), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        data.setExperience(event.getNewExp());
        checkLevelUp(player, race);
    }

    public boolean setLevel(Player player, Race race, int amount) {
        RaceData data = plugin.getRaceManager().getPlayerData(player, race);
        RaceLevelUpEvent event = new RaceLevelUpEvent(player, race, data.getLevel(), data.getLevel() + amount);
        Bukkit.getPluginManager().callEvent(event);

        if (race.isMaxLevel(data.getLevel()))
            return false;

        int newAmount = event.getNewLevel();

        if (event.getNewLevel() > race.getMaxLevel())
            newAmount = race.getMaxLevel();

        // Just incase amount is higher than 1
        for (int i = (event.getOldLevel() - 1); i < event.getNewLevel(); i++)
            data.setUnusedSkillpoints(data.getUnusedSkillpoints() + race.getSkillpointsForLevel(i));

        data.setLevel(newAmount);

        player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
        player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("levelup", "%prefix% Your %race% race has leveled up to level %level%!").replace("%level%", "" + newAmount)));
        return true;
    }

    public boolean canLevelUp(Player player, Race race) {
        RaceData data = plugin.getRaceManager().getPlayerData(player, race);
        return data.getExperience() >= race.getRequiredExperience(data.getLevel());
    }

    private void checkLevelUp(Player player, Race race) {
        RaceData data = plugin.getRaceManager().getPlayerData(player, race);
        int requiredExp = race.getRequiredExperience(data.getLevel());

        if (!canLevelUp(player, race))
            return;

        if (!setLevel(player, race, data.getLevel() + 1))
            return;

        data.setExperience(data.getExperience() - requiredExp);

        // Check if they can level up again just incase they were given an excess of experience
        checkLevelUp(player, race);
    }
}
