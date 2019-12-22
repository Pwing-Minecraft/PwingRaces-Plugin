package net.pwing.races.race.skilltree;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.utilities.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PwingRaceSkilltree implements RaceSkilltree {

    private String internalName;
    private String name;

    private ItemStack icon;
    private ItemStack menuIcon;
    private int menuSlot;
    private int menuSize;

    private List<RaceSkilltreeElement> elements;

    public PwingRaceSkilltree(String key, FileConfiguration config) {
        this.internalName = key;

        loadDataFromConfig(config);
    }

    private void loadDataFromConfig(FileConfiguration config) {
        this.name = config.getString("name");
        this.icon = ItemUtil.readItemFromConfig("icon", config);
        if (icon == null)
            icon = new ItemStack(Material.STONE);

        this.menuIcon = ItemUtil.readItemFromConfig("menu-icon", config);
        if (menuIcon == null)
            menuIcon = icon;

        this.menuSlot = config.getInt("menu-slot", -1);
        this.menuSize = config.getInt("menu-size", 54);

        List<RaceSkilltreeElement> elements = new ArrayList<>();
        if (config.contains("elements")) {
            for (String elem : config.getConfigurationSection("elements").getKeys(false))
                elements.add(new PwingRaceSkilltreeElement(elem, "elements." + elem, config));
        }

        this.elements = elements;
    }
}
