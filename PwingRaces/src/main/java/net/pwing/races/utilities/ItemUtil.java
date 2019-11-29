package net.pwing.races.utilities;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.builder.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemUtil {

    public static void addItem(Player player, ItemStack item) {
        // Check if their inventory is full
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }

    public static ItemStack fromString(Race race, String str) {
        ItemStack raceItem = race.getRaceItems().get(str);
        if (raceItem == null)
            return fromString(str);

        return raceItem;
    }

    public static ItemStack fromString(String str) {
        if (str == null || str.isEmpty())
            return null;

        String string = str;
        RaceMaterial mat;
        String name = null;

        if (string.contains("|")) {
            String[] temp = string.split("\\|");
            string = temp[0];

            name = ChatColor.translateAlternateColorCodes('&', temp[1]);
        }

        mat = RaceMaterial.fromString(string.toUpperCase());

        if (mat == null)
            mat = RaceMaterial.STONE;

        ItemBuilder builder = new ItemBuilder(mat.parseItem());
        if (name != null) {
            builder.setName(name);
        }

        return builder.toItemStack();
    }

    public static ItemStack readItemFromConfig(String configPath, FileConfiguration config) {
        ItemBuilder builder = new ItemBuilder(Material.STONE);

        if (!config.contains(configPath))
            return null;

        for (String str : config.getConfigurationSection(configPath).getKeys(false)) {
            switch (str) {
                case "type":
                case "material":
                case "item":
                    builder = new ItemBuilder(RaceMaterial.fromString(config.getString(configPath + "." + str).toUpperCase()).parseItem());
                    break;
                case "durability":
                case "data":
                    builder.setDurability(config.getInt(configPath + "." + str));
                    break;
                case "custom-model-data":
                case "model-data":
                    builder.setCustomModelData(config.getInt(configPath + "." + str));
                    break;
                case "amount":
                    builder.setAmount(config.getInt(configPath + "." + str));
                    break;
                case "name":
                case "display-name":
                    builder.setName(config.getString(configPath + "." + str));
                    break;
                case "enchants":
                case "enchantments":
                    for (String enchant : config.getStringList(configPath + "." + str)) {
                        int level = 1;

                        String[] split = enchant.split(" ");
                        if (NumberUtil.isInteger(split[1]))
                            level = Integer.parseInt(split[1]);

                        if (!isEnchantment(split[0]))
                            break;

                        Enchantment enchantment = PwingRaces.getInstance().getCompatCodeHandler().getEnchantment(split[0].toUpperCase());
                        builder.addEnchantment(enchantment, level);
                    }
                    break;
                case "lore":
                    builder.setLore(config.getStringList(configPath + "." + str));
                    break;
                case "unbreakable":
                    builder.setUnbreakable(config.getBoolean(configPath + "." + str));
                    break;
                case "owner":
                case "head-owner":
                    builder = new ItemBuilder(HeadUtil.getPlayerHead(builder.toItemStack(), config.getString(configPath + "." + str)));
                    // builder.setOwner(config.getString(configPath + "." + str));
                    break;
                case "color":
                case "colour":
                    String[] colorSplit = config.getString(configPath + "." + str).split(",");
                    Color color = null;

                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(config.getString(configPath + "." + str));

                    builder.setColor(color);
                    break;
                case "item-flags":
                    for (String flag : config.getStringList(configPath + "." + str)) {
                        if (!isItemFlag(flag))
                            continue;

                        builder.addItemFlag(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                    break;
                case "effects":
                case "potion-effects":
                    for (String effect : config.getStringList(configPath + "." + str)) {
                        String[] effectSplit = effect.split(" ");
                        PotionEffectType effectType = PotionEffectType.getByName(effectSplit[0]);
                        if (effectType == null)
                            continue;

                        int duration = 0;
                        int amplifier = 0;

                        if (NumberUtil.isInteger(effectSplit[1]))
                            duration = Integer.parseInt(effectSplit[1]) * 20;

                        if (NumberUtil.isInteger(effectSplit[2]))
                            amplifier = Integer.parseInt(effectSplit[2]) - 1;

                        builder.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
                    }
                    break;
                default:
                    break;
            }

            // TODO: Add item attribute API
        }

        return builder.toItemStack();
    }

    private static Color fromHex(String hex) {
        java.awt.Color jColor = java.awt.Color.decode(hex);
        return Color.fromRGB(jColor.getRed(), jColor.getGreen(), jColor.getBlue());
    }

    public static boolean isEnchantment(String str) {
        return PwingRaces.getInstance().getCompatCodeHandler().getEnchantment(str.toUpperCase()) != null;
    }

    public static boolean isItemFlag(String str) {
        try {
            ItemFlag.valueOf(str.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }
}
