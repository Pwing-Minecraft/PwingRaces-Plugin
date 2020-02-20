package net.pwing.races.race;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.api.race.ability.RaceAbilityManager;
import net.pwing.races.api.race.attribute.RaceAttribute;
import net.pwing.races.api.race.menu.RaceIconData;
import net.pwing.races.api.race.permission.RacePermission;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.trigger.RaceTrigger;
import net.pwing.races.race.attribute.PwingRaceAttribute;
import net.pwing.races.race.menu.PwingRaceIconData;
import net.pwing.races.race.permission.PwingRacePermission;
import net.pwing.races.util.item.ItemUtil;
import net.pwing.races.util.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class PwingRace implements Race {

    private String name;
    private String displayName;

    private int maxLevel;

    private Location spawnLocation;

    private boolean requiresUnlock = false;

    private FileConfiguration raceConfig;

    private RaceIconData iconData;

    private Map<String, ItemStack> raceItems;
    private Map<Integer, String> skilltreeMap;
    private Map<String, List<RacePermission>> racePermissionsMap;
    private Map<String, List<RaceAttribute>> raceAttributesMap;
    private Map<Integer, Integer> raceLevelMap;
    private Map<Integer, Integer> raceSkillpointsMap;
    private Map<String, List<RaceTrigger>> raceTriggersMap;
    private Map<String, List<RaceAbility>> raceAbilitiesMap;

    // private RaceCommandExecutor executor;

    public PwingRace(RaceManager raceManager, FileConfiguration raceConfig) {
        loadDataFromConfig(raceManager, raceConfig);

        // this.executor = new IndividualRaceExecutor(raceManager.getPlugin(), this, name.toLowerCase());
    }

    public void loadDataFromConfig(RaceManager raceManager, FileConfiguration raceConfig) {
        this.raceConfig = raceConfig;

        this.name = raceConfig.getString("race.name");
        this.displayName = ChatColor.translateAlternateColorCodes('&', raceConfig.getString("race.display-name", name));

        this.maxLevel = raceConfig.getInt("race.max-level");

        this.spawnLocation = LocationUtil.fromString(raceConfig.getString("race.spawn-location", ""));

        this.requiresUnlock = raceConfig.getBoolean("race.require-unlock", false);
        this.skilltreeMap = new LinkedHashMap<>();

        this.raceItems = new LinkedHashMap<>();
        if (raceConfig.contains("race.items")) {
            for (String str : raceConfig.getConfigurationSection("race.items").getKeys(false)) {
                ItemStack stack = ItemUtil.readItemFromConfig("race.items." + str, raceConfig);
                if (stack == null)
                    continue;

                raceItems.put(str, stack);
            }
        }

        for (String str : raceConfig.getStringList("race.skilltrees")) {
            Optional<RaceSkilltree> skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(str.split(" ")[0]);

            if (!skilltree.isPresent()) {
                Bukkit.getLogger().warning("[PwingRaces] " + "Could not find skilltree " + str + " for race " + name + "!");
                continue;
            }

            skilltreeMap.put(Integer.parseInt(str.split(" ")[1]), skilltree.get().getInternalName());
        }

        this.raceLevelMap = new LinkedHashMap<>();
        this.raceSkillpointsMap = new LinkedHashMap<>();

        if (raceConfig.contains("race.levels")) {
            for (String str : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                raceLevelMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".xp", 0));
                raceSkillpointsMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".skillpoints", 0));
            }
        }

        this.raceTriggersMap = new LinkedHashMap<>();

        // Get triggers defined in the triggers section
        if (raceConfig.contains("race.triggers")) {
            for (String str : raceConfig.getConfigurationSection("race.triggers").getKeys(false)) {
                List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(str, new ArrayList<>());
                raceTriggers.add(new RaceTrigger(str, "race.triggers." + str, raceConfig, "none"));
                raceTriggersMap.put(str, raceTriggers);
            }
        }

        this.raceAttributesMap = new LinkedHashMap<>();

        // Get attributes defined in the attributes section
        if (raceConfig.contains("race.attributes")) {
            for (String str : raceConfig.getConfigurationSection("race.attributes").getKeys(false)) {
                List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(str, new ArrayList<>());
                raceAttributes.add(new PwingRaceAttribute(str, raceConfig.getString("race.attributes." + str), "none"));
                raceAttributesMap.put(str, raceAttributes);
            }
        }

        this.racePermissionsMap = new LinkedHashMap<>();

        // Get permissions defined in the permissions section
        if (raceConfig.contains("race.permissions")) {
            for (String str : raceConfig.getStringList("race.permissions")) {
                List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(str, new ArrayList<>());
                racePermissions.add(new PwingRacePermission(str, "none"));
                racePermissionsMap.put(str, racePermissions);
            }
        }

        this.raceAbilitiesMap = new LinkedHashMap<>();
        RaceAbilityManager abilityManager = raceManager.getAbilityManager();

        // Get abilities defined in the abilities section
        if (raceConfig.contains("race.abilities")) {
            for (String str : raceConfig.getConfigurationSection("race.abilities").getKeys(false)) {
                List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(str, new ArrayList<>());
                abilityManager.getAbility(str, "none", "race.abilities." + str, raceConfig)
                        .ifPresent(raceAbilities::add);

                raceAbilitiesMap.put(str, raceAbilities);
            }
        }

        // Get sections in the elements section
        if (raceConfig.contains("race.elements")) {
            ConfigurationSection section = raceConfig.getConfigurationSection("race.elements");
            for (String elem : section.getKeys(false)) {
                ConfigurationSection elementSection = section.getConfigurationSection(elem);
                if (elementSection.contains("abilities")) {
                    for (String ability : elementSection.getConfigurationSection("abilities").getKeys(false)) {
                        List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<>());
                        abilityManager.getAbility(ability, elem, "race.elements." + elem + ".abilities." + ability, raceConfig)
                                .ifPresent(raceAbilities::add);

                        raceAbilitiesMap.put(ability, raceAbilities);
                    }
                }

                if (elementSection.contains("permissions")) {
                    for (String permission : elementSection.getStringList("permissions")) {
                        List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<>());
                        racePermissions.add(new PwingRacePermission(permission, elem));
                        racePermissionsMap.put(permission, racePermissions);
                    }
                }

                if (elementSection.contains("attributes")) {
                    for (String attribute : elementSection.getConfigurationSection("attributes").getKeys(false)) {
                        List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<>());
                        raceAttributes.add(new PwingRaceAttribute(attribute, elementSection.getString("attributes." + attribute), elem));
                        raceAttributesMap.put(attribute, raceAttributes);
                    }
                }

                if (elementSection.contains("triggers")) {
                    for (String trigger : elementSection.getConfigurationSection("triggers").getKeys(false)) {
                        List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<>());
                        raceTriggers.add(new RaceTrigger(trigger, "race.elements." + elem + ".triggers." + trigger, raceConfig, elem));
                        raceTriggersMap.put(trigger, raceTriggers);
                    }
                }
            }
        }

        // Get sections in the level section
        if (raceConfig.contains("race.levels")) {
            ConfigurationSection section = raceConfig.getConfigurationSection("race.levels");
            for (String level : section.getKeys(false)) {
                ConfigurationSection levelSection = section.getConfigurationSection(level);
                if (levelSection.contains("abilities")) {
                    for (String ability : levelSection.getConfigurationSection("abilities").getKeys(false)) {
                        List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<>());
                        abilityManager.getAbility(ability, level, "race.levels." + level + ".abilities." + ability, raceConfig)
                                .ifPresent(raceAbilities::add);

                        raceAbilitiesMap.put(ability, raceAbilities);
                    }
                }

                if (levelSection.contains("permissions")) {
                    for (String permission : levelSection.getStringList("permissions")) {
                        List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<>());
                        racePermissions.add(new PwingRacePermission(permission, "level" + level));
                        racePermissionsMap.put(permission, racePermissions);
                    }
                }

                if (levelSection.contains("attributes")) {
                    for (String attribute : levelSection.getConfigurationSection("attributes").getKeys(false)) {
                        List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<>());
                        raceAttributes.add(new PwingRaceAttribute(attribute, levelSection.getString("attributes." + attribute), "level" + level));
                        raceAttributesMap.put(attribute, raceAttributes);
                    }
                }

                if (levelSection.contains("triggers")) {
                    for (String trigger : levelSection.getConfigurationSection("triggers").getKeys(false)) {
                        List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<>());
                        raceTriggers.add(new RaceTrigger(trigger, "race.levels." + level + ".triggers." + trigger, raceConfig, "level" + level));
                        raceTriggersMap.put(trigger, raceTriggers);
                    }
                }
            }
        }

        ItemStack iconUnlocked = ItemUtil.readItemFromConfig("race.gui.icon", raceConfig);
        if (iconUnlocked == null) {
            Bukkit.getLogger().warning("[PwingRaces] " + "Could not load icon for race " + name + ". Please make sure your config is correct!");
            return;
        }

        ItemStack iconSelected = iconUnlocked;
        if (raceConfig.contains("race.gui.icon-selected"))
            iconSelected = ItemUtil.readItemFromConfig("race.gui.icon-selected", raceConfig);

        ItemStack iconLocked = iconUnlocked;
        if (raceConfig.contains("race.gui.icon-locked"))
            iconLocked = ItemUtil.readItemFromConfig("race.gui.icon-locked", raceConfig);

        int iconSlot = raceConfig.getInt("race.gui.slot", 0);
        this.iconData = new PwingRaceIconData(iconUnlocked, iconLocked, iconSelected, iconSlot);
    }

    public boolean doesRequireUnlock() {
        return requiresUnlock;
    }

    public Optional<Location> getSpawnLocation() {
        return Optional.ofNullable(spawnLocation);
    }

    public boolean isMaxLevel(int level) {
        if (raceLevelMap.isEmpty())
            return true;

        return raceLevelMap.size() < level;
    }

    public int getRequiredExperience(int level) {
        if (!raceLevelMap.containsKey(level))
            return 0;

        return raceLevelMap.get(level);
    }

    public int getSkillpointsForLevel(int level) {
        if (raceSkillpointsMap.containsKey(level))
            return raceSkillpointsMap.get(level);

        return 0;
    }

    // public RaceCommandExecutor getExecutor() {
    // 	return executor;
    // }

    // public void setExecutor(RaceCommandExecutor executor) {
    //	this.executor = executor;
    // }
}
