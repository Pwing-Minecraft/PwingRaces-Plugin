package net.pwing.races.util.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * A near copy of {@link MaterialData} which is subject to removal.
 */
@Getter
@AllArgsConstructor
public class SafeMaterialData {

    private Material material;
    private int data;
}
