package net.pwing.races.race.ability;

import java.util.ArrayList;
import java.util.List;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.ability.RaceAbility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.utilities.ItemUtil;

public abstract class PwingRaceAbility implements RaceAbility, Listener {

    protected PwingRaces plugin;

    protected String requirement;
    protected String internalName;

    protected double cooldown;

    protected ItemStack[] abilityItems;
    protected ItemStack[] leftClickAbilityItems;
    protected ItemStack[] consumeAbilityItems;

    protected String requiredPermission;
    protected String cooldownMessage;

    protected boolean cancelDefaultAction;
    protected boolean overrideDefaultAction;

    protected List<String> allowedWorlds = new ArrayList<String>();
    protected List<String> passives = new ArrayList<String>();

    public PwingRaceAbility(PwingRaces plugin, String internalName, String configPath, FileConfiguration config, String requirement) {
        this.plugin = plugin;
        this.internalName = internalName;
        this.requirement = requirement;

        loadDataFromConfig(configPath, config);
    }

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

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public ItemStack[] getAbilityItems() {
        return abilityItems;
    }

    public void setAbilityItems(ItemStack[] abilityItems) {
        this.abilityItems = abilityItems;
    }

    public ItemStack[] getLeftClickAbilityItems() {
        return leftClickAbilityItems;
    }

    public void setLeftClickAbilityItems(ItemStack[] leftClickAbilityItems) {
        this.leftClickAbilityItems = leftClickAbilityItems;
    }

    public ItemStack[] getConsumeAbilityItems() {
        return consumeAbilityItems;
    }

    public void setConsumeAbilityItems(ItemStack[] consumeAbilityItems) {
        this.consumeAbilityItems = consumeAbilityItems;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }

    public void setCooldownMessage(String cooldownMessage) {
        this.cooldownMessage = cooldownMessage;
    }

    public boolean isDefaultActionOverriden() {
        return overrideDefaultAction;
    }

    public void setOverrideDefaultAction(boolean overrideDefaultAction) {
        this.overrideDefaultAction = overrideDefaultAction;
    }

    public boolean isDefaultActionCancelled() {
        return cancelDefaultAction;
    }

    public void setCancelDefaultAction(boolean cancelDefaultAction) {
        this.cancelDefaultAction = cancelDefaultAction;
    }

    public List<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    public void setAllowedWorlds(List<String> allowedWorlds) {
        this.allowedWorlds = allowedWorlds;
    }

    public List<String> getPassives() {
        return passives;
    }

    public void setPassives(List<String> passives) {
        this.passives = passives;
    }

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
