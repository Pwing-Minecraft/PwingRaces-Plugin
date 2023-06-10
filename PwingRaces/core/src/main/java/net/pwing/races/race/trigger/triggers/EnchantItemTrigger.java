package net.pwing.races.race.trigger.triggers;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.trigger.RaceTriggerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

@AllArgsConstructor
public class EnchantItemTrigger implements Listener {

    private RaceTriggerManager triggerManager;

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled())
            return;

        triggerManager.runTriggers(event.getEnchanter(), "enchant-item");
        triggerManager.runTriggers(event.getEnchanter(), "enchant-item " + event.getItem().getType().name().toLowerCase());
    }
}
