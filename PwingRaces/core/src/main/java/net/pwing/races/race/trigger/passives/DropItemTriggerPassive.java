package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.util.item.ItemUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DropItemTriggerPassive extends RaceTriggerPassive {

    private PwingRaces plugin;

    public DropItemTriggerPassive(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String[] trigger) {
        String itemKey = String.join("", trigger).replace(name + " ", "");

        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer.getRace().isEmpty())
            return;

        Race race = racePlayer.getRace().get();
        ItemStack item = ItemUtil.readItem(race, itemKey);

        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }
}
