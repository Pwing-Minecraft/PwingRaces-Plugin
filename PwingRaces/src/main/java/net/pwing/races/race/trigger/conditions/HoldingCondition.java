package net.pwing.races.race.trigger.conditions;

import net.pwing.races.api.race.trigger.condition.RaceCondition;

import net.pwing.races.util.item.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HoldingCondition implements RaceCondition {

    @Override
    public boolean check(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        ItemStack stack = ItemUtil.fromString(builder.toString());
        if (stack == null) {
            return false;
        }
        return player.getInventory().getItemInMainHand().equals(stack);
    }
}
