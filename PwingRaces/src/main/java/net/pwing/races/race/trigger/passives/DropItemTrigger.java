package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.PwingRace;
import net.pwing.races.race.trigger.RaceTriggerPassive;
import net.pwing.races.utilities.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DropItemTrigger extends RaceTriggerPassive {

    public DropItemTrigger(PwingRaces plugin, String name) {
        super(plugin, name);
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String itemKey = trigger.replace(name + " ", "");

        PwingRace race = plugin.getRaceManager().getRacePlayer(player).getActiveRace();
        ItemStack item = race.getRaceItems().get(itemKey);
        if (item == null) {
            item = ItemUtil.fromString(itemKey);
        }

        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }
}
