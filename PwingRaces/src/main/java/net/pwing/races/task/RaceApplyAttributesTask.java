package net.pwing.races.task;

import net.pwing.races.api.race.attribute.RaceAttributeManager;

import org.bukkit.Bukkit;

public class RaceApplyAttributesTask implements Runnable {

    private RaceAttributeManager attributeManager;

    public RaceApplyAttributesTask(RaceAttributeManager attributeManager) {
        this.attributeManager = attributeManager;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(attributeManager::applyAttributeBonuses);
    }
}
