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

import java.util.Arrays;
import java.util.List;
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

    public static ItemStack readItem(Race race, String str) {
        RaceItemDefinition raceItem = race.getItemDefinitions().get(str);
        if (raceItem == null) {
            return readItem(str);
        }

        return raceItem.itemStack();
    }

    public static ItemStack[] readItems(String str) {
        String[] split = str.split("(?![^)(]*\\([^)(]*?\\)\\)),(?![^{]*})");
        ItemStack[] items = new ItemStack[split.length];
        for (int i = 0; i < split.length; i++) {
            items[i] = readItem(split[i]);
        }

        return items;
    }

    public static ItemStack readItem(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }

        if (str.contains("|")) {
            return fromStringLegacy(str);
        }

        String[] split = str.split("\\{");
        Material material = Material.getMaterial(split[0].toUpperCase(Locale.ROOT));
        if (material == null) {
            PwingRaces.getInstance().getLogger().warning("Invalid material " + split[0] + "!");
            return null;
        }

        ItemBuilder builder = new ItemBuilder(material);
        if (split.length == 1) {
            return builder.toItemStack();
        }

        String data = split[1].replace("\\}", "");
        for (String meta : data.split(";")) {
            String[] option = meta.split("=");
            switch (option[0]) {
                case "durability", "data" -> builder.setDurability(NumberUtil.getInteger(option[1]));
                case "custom-model-data", "model-data" ->
                        builder.setCustomModelData(NumberUtil.getInteger(option[1]));
                case "amount" -> builder.setAmount(NumberUtil.getInteger(option[1]));
                case "name", "display-name" -> builder.setName(option[1]);
                case "enchants", "enchantments" -> {
                    for (String enchant : getList(meta)) {
                        String[] del = enchant.split(":");

                        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.fromString(del[0].toLowerCase(Locale.ROOT)));
                        if (enchantment == null) {
                            enchantment = EnchantmentWrapper.getByName(del[0].toUpperCase(Locale.ROOT));
                        }

                        if (enchantment != null) {
                            builder.addEnchantment(enchantment, NumberUtil.getInteger(del[1]));
                        }
                    }
                }
                case "lore" -> builder.setLore(getList(meta));
                case "unbreakable" -> builder.setUnbreakable(Boolean.parseBoolean(option[1]));
                case "owner", "head-owner" ->
                        builder = new ItemBuilder(HeadUtil.getPlayerHead(builder.toItemStack(), option[1]));
                case "color", "colour" -> {
                    String[] colorSplit = option[1].split(",");
                    Color color = null;
                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(option[1]);
                    builder.setColor(color);
                }
                case "item-flags" -> {
                    for (String flag : getList(meta)) {
                        if (!isItemFlag(flag))
                            continue;

                        builder.addItemFlag(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }
                case "effects", "potion-effects" -> {
                    for (String effect : getList(meta)) {
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
                }
                default -> {
                }
            }
        }

        return builder.toItemStack();
    }

    public static ItemStack fromStringLegacy(String str) {
        PwingRaces.getInstance().getLogger().warning("Reading item " + str + " using the legacy format! Please see the wiki for the up-to-date format.");

        String[] temp = str.split("\\|");
        String item = temp[0];
        String name = ChatColor.translateAlternateColorCodes('&', temp[1]);

        Material type = Material.getMaterial(item.toUpperCase(Locale.ROOT));
        ItemBuilder builder = new ItemBuilder(type);
        builder.setName(name);

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
                case "type", "material", "item" -> {
                    String matName = config.getString(configPath + "." + str).toUpperCase(Locale.ROOT);
                    Material material = Material.getMaterial(matName);
                    if (material == null) {
                        PwingRaces.getInstance().getLogger().warning("Invalid material " + matName + " at path " + configPath + "! Defaulting to stone...");
                        material = Material.STONE;
                    }
                    builder = new ItemBuilder(material);
                }
                case "durability", "data" -> builder.setDurability(config.getInt(configPath + "." + str));
                case "custom-model-data", "model-data" ->
                        builder.setCustomModelData(config.getInt(configPath + "." + str));
                case "amount" -> builder.setAmount(config.getInt(configPath + "." + str));
                case "name", "display-name" -> builder.setName(config.getString(configPath + "." + str));
                case "enchants", "enchantments" -> {
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
                }
                case "lore" -> builder.setLore(config.getStringList(configPath + "." + str));
                case "unbreakable" -> builder.setUnbreakable(config.getBoolean(configPath + "." + str));
                case "owner", "head-owner" ->
                        builder = new ItemBuilder(HeadUtil.getPlayerHead(builder.toItemStack(), config.getString(configPath + "." + str)));
                case "color", "colour" -> {
                    String[] colorSplit = config.getString(configPath + "." + str).split(",");
                    Color color = null;
                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(config.getString(configPath + "." + str));
                    builder.setColor(color);
                }
                case "item-flags" -> {
                    for (String flag : config.getStringList(configPath + "." + str)) {
                        if (!isItemFlag(flag))
                            continue;

                        builder.addItemFlag(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }
                case "effects", "potion-effects" -> {
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
                }
                default -> {
                }
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

    private static List<String> getList(String value) {
        return Arrays.asList(value.split("=")[1].replace("[", "")
                .replace("]", "").split(","));
    }
}
