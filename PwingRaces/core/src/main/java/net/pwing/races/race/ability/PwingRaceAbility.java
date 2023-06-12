package net.pwing.races.race.ability;

import com.google.common.collect.ArrayListMultimap;
import net.pwing.races.PwingRaces;
import net.pwing.races.api.PwingRacesAPI;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.util.item.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            this.abilityItems = ItemUtil.readItems(config.getString(configPath + ".ability-item"));
        } else {
            this.abilityItems = new ItemStack[0];
        }

        if (config.contains(configPath + ".left-click-ability-item")) {
            this.leftClickAbilityItems = ItemUtil.readItems(config.getString(configPath + ".left-click-ability-item"));
        } else {
            this.leftClickAbilityItems = new ItemStack[0];
        }

        if (config.contains(configPath + ".consume-ability-item")) {
            this.consumeAbilityItems = ItemUtil.readItems(config.getString(configPath + ".consume-ability-item"));
        } else {
            this.consumeAbilityItems = new ItemStack[0];
        }

        this.requiredPermission = config.getString(configPath + ".required-permission", "none");
        this.cooldownMessage = config.getString(configPath + ".cooldown-message");
        this.cancelDefaultAction = config.getBoolean(configPath + ".cancel-default-action", true);
        this.overrideDefaultAction = config.getBoolean(configPath + ".override-default-action", false);

        this.allowedWorlds = config.getStringList(configPath + ".allowed-worlds");
        this.passives = ArrayListMultimap.create();
        for (String passive : config.getStringList(configPath + ".run-passives")) {
            String passiveName = passive.split(" ")[0];
            if (PwingRacesAPI.getTriggerManager().getTriggerPassives().containsKey(passiveName)) {
                this.passives.put(passive, PwingRacesAPI.getTriggerManager().getTriggerPassives().get(passiveName));
            }
        }

        this.conditions = ArrayListMultimap.create();
        for (String condition : config.getStringList(configPath + ".conditions")) {
            String conditionName = condition.split(" ")[0];
            if (PwingRacesAPI.getTriggerManager().getConditions().containsKey(conditionName)) {
                this.conditions.put(condition, PwingRacesAPI.getTriggerManager().getConditions().get(conditionName));
            }
        }
    }

    @Override
    public void saveDataToConfig(String configPath, FileConfiguration config) {
        config.set(configPath + ".cooldown", cooldown);
        config.set(configPath + ".ability-item", ItemUtil.writeItems(abilityItems));
        config.set(configPath + ".left-click-ability-item", ItemUtil.writeItems(leftClickAbilityItems));
        config.set(configPath + ".consume-ability-item", ItemUtil.writeItems(consumeAbilityItems));
        config.set(configPath + ".required-permission", requiredPermission);
        config.set(configPath + ".cooldown-message", cooldownMessage);
        config.set(configPath + ".cancel-default-action", cancelDefaultAction);
        config.set(configPath + ".override-default-action", overrideDefaultAction);
        config.set(configPath + ".allowed-worlds", allowedWorlds);
        config.set(configPath + ".run-passives", passives.keySet());
        config.set(configPath + ".conditions", conditions.keySet());
    }

    public abstract boolean runAbility(Player player);

    // Override the methods in RaceAbility as we have our own version code to check this already
    @Override
    public boolean canRun(Player player, ItemStack stack) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (stack == null)
            return false;

        if (stack.getType() != hand.getType())
            return false;

        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            if (!hand.hasItemMeta())
                return false;

            if (!hand.getItemMeta().hasDisplayName())
                return false;

            return hand.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());
        }

        return true;
    }
}
