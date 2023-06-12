package net.pwing.races.util.item;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceItemDefinition;
import net.pwing.races.util.math.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
        Material material = Registry.MATERIAL.get(NamespacedKey.fromString(split[0].toLowerCase(Locale.ROOT)));
        if (material == null) {
            PwingRaces.getInstance().getLogger().warning("Invalid material " + split[0] + "!");
            return null;
        }

        ItemBuilder builder = ItemBuilder.builder(material);
        if (split.length == 1) {
            return builder.build();
        }

        String data = split[1].replace("\\}", "");
        for (String meta : data.split(";")) {
            String[] option = meta.split("=");
            switch (option[0]) {
                case "durability", "data", "damage" -> builder.durability(NumberUtil.getInteger(option[1]));
                case "custom-model-data", "model-data" ->
                        builder.customModelData(NumberUtil.getInteger(option[1]));
                case "amount" -> builder.amount(NumberUtil.getInteger(option[1]));
                case "name", "display-name" -> builder.name(option[1]);
                case "enchants", "enchantments" -> {
                    for (String enchant : getList(meta)) {
                        String[] del = enchant.split(":");

                        Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.fromString(del[0].toLowerCase(Locale.ROOT)));
                        if (enchantment == null) {
                            enchantment = EnchantmentWrapper.getByName(del[0].toUpperCase(Locale.ROOT));
                        }

                        if (enchantment != null) {
                            builder.enchantment(enchantment, NumberUtil.getInteger(del[1]));
                        }
                    }
                }
                case "lore" -> builder.lore(getList(meta));
                case "unbreakable" -> builder.unbreakable(Boolean.parseBoolean(option[1]));
                case "owner", "head-owner" ->
                        builder = ItemBuilder.builder(HeadUtil.getPlayerHead(builder.build(), option[1]));
                case "color", "colour" -> {
                    String[] colorSplit = option[1].split(",");
                    Color color = null;
                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(option[1]);
                    builder.color(color);
                }
                case "item-flags" -> {
                    for (String flag : getList(meta)) {
                        if (!isItemFlag(flag))
                            continue;

                        builder.itemFlags(ItemFlag.valueOf(flag.toUpperCase()));
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

                        builder.potionEffect(new PotionEffect(effectType, duration, amplifier));
                    }
                }
                default -> {
                }
            }
        }

        return builder.build();
    }

    public static String writeItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        StringBuilder str = new StringBuilder();
        str.append(item.getType().getKey());
        List<String> serializedProperties = new ArrayList<>();

        if (item.getAmount() > 1) {
            serializedProperties.add("amount=" + item.getAmount());
        }

        if (meta != null) {
            serializedProperties.add("name=" + meta.getDisplayName());
            if (meta instanceof Damageable damageable) {
                serializedProperties.add("damage=" + damageable.getDamage());
            }

            if (meta.hasCustomModelData()) {
                serializedProperties.add("custom-model-data=" + meta.getCustomModelData());
            }

            if (meta.hasDisplayName()) {
                serializedProperties.add("name=" + meta.getDisplayName());
            }

            if (meta.hasEnchants()) {
                serializedProperties.add("enchants=[" + meta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getKey() + ":" + entry.getValue()).collect(Collectors.joining(",")) + "]");
            }

            if (meta.hasLore()) {
                serializedProperties.add("lore=[" + meta.getLore().stream().collect(Collectors.joining(",")) + "]");
            }

            if (meta.isUnbreakable()) {
                serializedProperties.add("unbreakable=true");
            }

            if (meta instanceof SkullMeta skullMeta) {
                if (skullMeta.hasOwner()) {
                    serializedProperties.add("owner=" + skullMeta.getOwnerProfile().getName());
                }
            }

            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                serializedProperties.add("color=" + leatherArmorMeta.getColor().getRed() + "," + leatherArmorMeta.getColor().getGreen() + "," + leatherArmorMeta.getColor().getBlue());
            }

            if (meta instanceof PotionMeta potionMeta) {
                serializedProperties.add("potion-effects=[" + potionMeta.getCustomEffects().stream().map(effect -> effect.getType().getName() + " " + effect.getDuration() / 20 + " " + effect.getAmplifier()).collect(Collectors.joining(",")) + "]");
            }

            if (meta.getItemFlags().size() > 0) {
                serializedProperties.add("item-flags=[" + meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.joining(",")) + "]");
            }
        }

        if (serializedProperties.isEmpty()) {
            return str.toString();
        }

        str.append("{");
        str.append(String.join(";", serializedProperties));
        str.append("}");
        return str.toString();
    }

    public static String writeItems(ItemStack[] items) {
        List<String> str = new ArrayList<>();
        for (ItemStack item : items) {
            str.add(writeItem(item));
        }

        return String.join(",", str);
    }

    public static ItemStack fromStringLegacy(String str) {
        PwingRaces.getInstance().getLogger().warning("Reading item " + str + " using the legacy format! Please see the wiki for the up-to-date format.");

        String[] temp = str.split("\\|");
        String item = temp[0];
        String name = ChatColor.translateAlternateColorCodes('&', temp[1]);

        Material type = Material.getMaterial(item.toUpperCase(Locale.ROOT));
        ItemBuilder builder = ItemBuilder.builder(type);
        builder.name(name);

        return builder.build();
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
        ItemBuilder builder = ItemBuilder.builder(Material.STONE);

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
                    builder = ItemBuilder.builder(material);
                }
                case "durability", "data", "damage" -> builder.durability(config.getInt(configPath + "." + str));
                case "custom-model-data", "model-data" ->
                        builder.customModelData(config.getInt(configPath + "." + str));
                case "amount" -> builder.amount(config.getInt(configPath + "." + str));
                case "name", "display-name" -> builder.name(config.getString(configPath + "." + str));
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
                            builder.enchantment(enchantment, level);
                        }
                    }
                }
                case "lore" -> builder.lore(config.getStringList(configPath + "." + str));
                case "unbreakable" -> builder.unbreakable(config.getBoolean(configPath + "." + str));
                case "owner", "head-owner" ->
                        builder = ItemBuilder.builder(HeadUtil.getPlayerHead(builder.build(), config.getString(configPath + "." + str)));
                case "color", "colour" -> {
                    String[] colorSplit = config.getString(configPath + "." + str).split(",");
                    Color color = null;
                    if (colorSplit.length == 3)
                        color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                    else
                        color = fromHex(config.getString(configPath + "." + str));
                    builder.color(color);
                }
                case "item-flags" -> {
                    for (String flag : config.getStringList(configPath + "." + str)) {
                        if (!isItemFlag(flag))
                            continue;

                        builder.itemFlags(ItemFlag.valueOf(flag.toUpperCase()));
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

                        builder.potionEffect(new PotionEffect(effectType, duration, amplifier));
                    }
                }
                default -> {
                }
            }

            // TODO: Add item attribute API
        }

        return builder.build();
    }

    public static void writeRaceItemToConfig(String configPath, RaceItemDefinition item, FileConfiguration config) {
        writeItemToConfig(configPath, item.itemStack(), config);
        config.set(configPath + ".give-to-player", item.giveToPlayer());
    }

    public static void writeItemToConfig(String configPath, ItemStack item, FileConfiguration config) {
        ItemMeta meta = item.getItemMeta();

        config.set(configPath + ".type", item.getType().toString());
        config.set(configPath + ".amount", item.getAmount());
        if (meta instanceof Damageable damageable) {
            config.set(configPath + ".damage", damageable.getDamage());
        }

        if (meta != null) {
            config.set(configPath + ".name", meta.getDisplayName());
            config.set(configPath + ".lore", meta.getLore());
            config.set(configPath + ".unbreakable", meta.isUnbreakable());
            config.set(configPath + ".item-flags", meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
            config.set(configPath + ".enchantments", meta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getKey() + " " + entry.getValue()).collect(Collectors.toList()));
            if (meta instanceof PotionMeta potionMeta) {
                config.set(configPath + ".potion-effects", potionMeta.getCustomEffects().stream().map(effect -> effect.getType().getName() + " " + effect.getDuration() / 20 + " " + effect.getAmplifier()).collect(Collectors.toList()));
            }

            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                config.set(configPath + ".color", leatherArmorMeta.getColor().getRed() + "," + leatherArmorMeta.getColor().getGreen() + "," + leatherArmorMeta.getColor().getBlue());
            }

            if (meta instanceof SkullMeta skullMeta && skullMeta.getOwnerProfile() != null) {
                config.set(configPath + ".owner", skullMeta.getOwnerProfile().getName());
            }

            if (meta.hasCustomModelData()) {
                config.set(configPath + ".custom-model-data", meta.getCustomModelData());
            }
        }
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
