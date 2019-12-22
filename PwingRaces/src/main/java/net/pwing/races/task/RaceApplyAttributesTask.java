package net.pwing.races.task;

import lombok.AllArgsConstructor;

import net.pwing.races.api.race.attribute.RaceAttributeManager;

import org.bukkit.Bukkit;

@AllArgsConstructor
public class RaceApplyAttributesTask implements Runnable {

    private RaceAttributeManager attributeManager;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(attributeManager::applyAttributeBonuses);
    }
}
