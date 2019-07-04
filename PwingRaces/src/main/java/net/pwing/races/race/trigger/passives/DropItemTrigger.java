package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.ItemUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DropItemTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public DropItemTrigger(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String itemKey = trigger.replace(name + " ", "");

        Race race = plugin.getRaceManager().getRacePlayer(player).getActiveRace();
        ItemStack item = race.getRaceItems().get(itemKey);
        if (item == null) {
            item = ItemUtil.fromString(itemKey);
        }

        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }
}
