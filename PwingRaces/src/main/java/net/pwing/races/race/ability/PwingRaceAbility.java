package net.pwing.races.race.ability;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.ability.RaceAbility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.utilities.ItemUtil;

public abstract class PwingRaceAbility extends RaceAbility {

    protected PwingRaces plugin;

    public PwingRaceAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        super(internalName, configPath, config, requirement);

        this.plugin = plugin;
    }

    // Override the methods in RaceAbility as we have our own code to check this already
    @Override
    public void loadDataFromConfig(String configPath, FileConfiguration config) {
        this.cooldown = config.getDouble(configPath + ".cooldown", 0);

        if (config.contains(configPath + ".ability-item")) {
            String[] abilityItemStr = config.getString(configPath + ".ability-item").split(",");
            this.abilityItems = new ItemStack[abilityItemStr.length];

            for (int i = 0; i < abilityItemStr.length; i++) {
                ItemStack stack = ItemUtil.fromString(abilityItemStr[i]);
                if (stack != null)
                    this.abilityItems[i] = stack;
            }
        } else {
            this.abilityItems = new ItemStack[0];
        }

        if (config.contains(configPath + ".left-click-ability-item")) {
            String[] leftClickAbilityItemStr = config.getString(configPath + ".left-click-ability-item").split(",");
            this.leftClickAbilityItems = new ItemStack[leftClickAbilityItemStr.length];

            for (int i = 0; i < leftClickAbilityItemStr.length; i++) {
                ItemStack stack = ItemUtil.fromString(leftClickAbilityItemStr[i]);
                if (stack != null)
                    this.leftClickAbilityItems[i] = stack;
            }
        } else {
            this.leftClickAbilityItems = new ItemStack[0];
        }

        if (config.contains(configPath + ".consume-ability-item")) {
            String[] consumeAbilityItemStr = config.getString(configPath + ".consume-ability-item").split(",");
            this.consumeAbilityItems = new ItemStack[consumeAbilityItemStr.length];

            for (int i = 0; i < consumeAbilityItemStr.length; i++) {
                ItemStack stack = ItemUtil.fromString(consumeAbilityItemStr[i]);
                if (stack != null)
                    this.consumeAbilityItems[i] = stack;
            }
        } else {
            this.consumeAbilityItems = new ItemStack[0];
        }

        this.requiredPermission = config.getString(configPath + ".required-permission", "none");
        this.cooldownMessage = config.getString(configPath + ".cooldown-message");
        this.cancelDefaultAction = config.getBoolean(configPath + ".cancel-default-action", true);
        this.overrideDefaultAction = config.getBoolean(configPath + ".override-default-action", false);

        this.allowedWorlds = config.getStringList(configPath + ".allowed-worlds");
        this.passives = config.getStringList(configPath + ".run-passives");
    }

    public abstract boolean runAbility(Player player);

    // Override the methods in RaceAbility as we have our own version code to check this already
    @Override
    public boolean canRun(Player player, ItemStack stack) {
        ItemStack hand = plugin.getCompatCodeHandler().getItemInMainHand(player);
        if (hand == null)
            return false;

        if (stack == null)
            return false;

        if (stack.getType() != hand.getType())
            return false;

        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            if (!hand.hasItemMeta())
                return false;

            if (!hand.getItemMeta().hasDisplayName())
                return false;

            if (!hand.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName()))
                return false;
        }

        return true;
    }
}
