package net.pwing.races.util.item;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceItemDefinition;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

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
        Material mat;
        String name = null;

        if (string.contains("|")) {
            String[] temp = string.split("\\|");
            string = temp[0];

            name = ChatColor.translateAlternateColorCodes('&', temp[1]);
        }

        mat = Material.getMaterial(string.toUpperCase());
        ItemBuilder builder = new ItemBuilder(mat);
        if (name != null) {
            builder.setName(name);
        }

        return builder.toItemStack();
    }

    public static RaceItemDefinition readRaceItemFromConfig(String configPath, FileConfiguration config) {
        ItemStack item = readItemFromConfig(configPath, config);
        if (item == null) {
            return null;
        }

        boolean giveToPlayer = config.getBoolean(configPath + ".give-to-player", true);
        return new RaceItemDefinition(item, giveToPlayer);
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
                    String matName = config.getString(configPath + "." + str).toUpperCase(Locale.ROOT);
                    Material material = Material.getMaterial(matName);
                    if (material == null) {
                        PwingRaces.getInstance().getLogger().warning("Invalid material " + matName + " at path " + configPath + "! Defaulting to stone...");
                        material = Material.STONE;
                    }

                    builder = new ItemBuilder(material);
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

                        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.fromString(split[0].toLowerCase(Locale.ROOT)));
                        if (enchantment == null) {
                            enchantment = EnchantmentWrapper.getByName(split[0].toUpperCase());
                        }

                        if (enchantment != null) {
                            builder.addEnchantment(enchantment, level);
                        }
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

    public static boolean isItemFlag(String str) {
        try {
            ItemFlag.valueOf(str.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {/* do nothing */}

        return false;
    }
}
