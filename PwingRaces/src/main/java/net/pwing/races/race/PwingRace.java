package net.pwing.races.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.pwing.races.utilities.AttributeUtil;
import net.pwing.races.utilities.ItemUtil;
import net.pwing.races.utilities.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class PwingRace implements Race {

    private String name;
    private String displayName;

    private int maxLevel;

    private Location spawnLocation;

    private boolean requireUnlock = false;

    private FileConfiguration raceConfig;

    private RaceIconData iconData;

    private Map<String, ItemStack> raceItems;

    private Map<Integer, String> raceSkilltreeMap;
    private Map<String, List<RacePermission>> racePermissionsMap;
    private Map<String, List<RaceAttribute>> raceAttributesMap;
    private Map<Integer, Integer> raceLevelMap;
    private Map<Integer, Integer> raceSkillpointMap;

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

        this.requireUnlock = raceConfig.getBoolean("race.require-unlock", false);
        this.raceSkilltreeMap = new HashMap<>();

        this.raceItems = new HashMap<>();
        if (raceConfig.contains("race.items")) {
            for (String str : raceConfig.getConfigurationSection("race.items").getKeys(false)) {
                ItemStack stack = ItemUtil.readItemFromConfig("race.items." + str, raceConfig);
                if (stack == null)
                    continue;

                raceItems.put(str, stack);
            }
        }

        for (String str : raceConfig.getStringList("race.skilltrees")) {
            RaceSkilltree skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(str.split(" ")[0]);

            if (skilltree == null) {
                Bukkit.getLogger().warning("[PwingRaces] " + "Could not find skilltree " + str + " for race " + name + "!");
                continue;
            }

            raceSkilltreeMap.put(Integer.parseInt(str.split(" ")[1]), skilltree.getInternalName());
        }

        this.raceLevelMap = new HashMap<>();
        this.raceSkillpointMap = new HashMap<>();

        if (raceConfig.contains("race.levels")) {
            for (String str : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                raceLevelMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".xp", 0));
                raceSkillpointMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".skillpoints", 0));
            }
        }

        this.raceTriggersMap = new HashMap<>();

        // Get triggers defined in the triggers section
        if (raceConfig.contains("race.triggers")) {
            for (String str : raceConfig.getConfigurationSection("race.triggers").getKeys(false)) {
                List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(str, new ArrayList<RaceTrigger>());
                raceTriggers.add(new RaceTrigger(str, "race.triggers." + str, raceConfig, "none"));
                raceTriggersMap.put(str, raceTriggers);
            }
        }

        this.raceAttributesMap = new HashMap<>();

        // Get attributes defined in the attributes section
        if (raceConfig.contains("race.attributes")) {
            for (String str : raceConfig.getConfigurationSection("race.attributes").getKeys(false)) {
                List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(str, new ArrayList<RaceAttribute>());
                raceAttributes.add(new PwingRaceAttribute(AttributeUtil.getAttributeName(str), raceConfig.getDouble("race.attributes." + str), "none"));
                raceAttributesMap.put(str, raceAttributes);
            }
        }

        this.racePermissionsMap = new HashMap<>();

        // Get permissions defined in the permissions section
        if (raceConfig.contains("race.permissions")) {
            for (String str : raceConfig.getStringList("race.permissions")) {
                List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(str, new ArrayList<RacePermission>());
                racePermissions.add(new PwingRacePermission(str, "none"));
                racePermissionsMap.put(str, racePermissions);
            }
        }

        this.raceAbilitiesMap = new HashMap<>();
        RaceAbilityManager abilityManager = raceManager.getAbilityManager();

        // Get abilities defined in the abilities section
        if (raceConfig.contains("race.abilities")) {
            for (String str : raceConfig.getConfigurationSection("race.abilities").getKeys(false)) {
                List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(str, new ArrayList<RaceAbility>());
                RaceAbility raceAbility = abilityManager.getAbility(str, "none", "race.abilities." + str, raceConfig);
                if (raceAbility != null)
                    raceAbilities.add(raceAbility);

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
                        List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<RaceAbility>());
                        RaceAbility raceAbility = abilityManager.getAbility(ability, elem, "race.elements." + elem + ".abilities." + ability, raceConfig);
                        if (raceAbility != null)
                            raceAbilities.add(raceAbility);

                        raceAbilitiesMap.put(ability, raceAbilities);
                    }
                }

                if (elementSection.contains("permissions")) {
                    for (String permission : elementSection.getStringList("permissions")) {
                        List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<RacePermission>());
                        racePermissions.add(new PwingRacePermission(permission, elem));
                        racePermissionsMap.put(permission, racePermissions);
                    }
                }

                if (elementSection.contains("attributes")) {
                    for (String attribute : elementSection.getConfigurationSection("attributes").getKeys(false)) {
                        List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<RaceAttribute>());
                        raceAttributes.add(new PwingRaceAttribute(AttributeUtil.getAttributeName(attribute), elementSection.getDouble("attributes." + attribute), elem));
                        raceAttributesMap.put(attribute, raceAttributes);
                    }
                }

                if (elementSection.contains("triggers")) {
                    for (String trigger : elementSection.getConfigurationSection("triggers").getKeys(false)) {
                        List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<RaceTrigger>());
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
                        List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<RaceAbility>());
                        RaceAbility raceAbility = abilityManager.getAbility(ability, level, "race.levels." + level + ".abilities." + ability, raceConfig);
                        if (raceAbility != null)
                            raceAbilities.add(raceAbility);

                        raceAbilitiesMap.put(ability, raceAbilities);
                    }
                }

                if (levelSection.contains("permissions")) {
                    for (String permission : levelSection.getStringList("permissions")) {
                        List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<RacePermission>());
                        racePermissions.add(new PwingRacePermission(permission, "level" + level));
                        racePermissionsMap.put(permission, racePermissions);
                    }
                }

                if (levelSection.contains("attributes")) {
                    for (String attribute : levelSection.getConfigurationSection("attributes").getKeys(false)) {
                        List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<RaceAttribute>());
                        raceAttributes.add(new PwingRaceAttribute(AttributeUtil.getAttributeName(attribute), levelSection.getDouble("attributes." + attribute), "level" + level));
                        raceAttributesMap.put(attribute, raceAttributes);
                    }
                }

                if (levelSection.contains("triggers")) {
                    for (String trigger : levelSection.getConfigurationSection("triggers").getKeys(false)) {
                        List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<RaceTrigger>());
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

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean doesRequireUnlock() {
        return requireUnlock;
    }

    public void setRequiresUnlock(boolean requireUnlock) {
        this.requireUnlock = requireUnlock;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean hasSpawnLocation() {
        return spawnLocation != null;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Map<Integer, String> getSkilltreeMap() {
        return raceSkilltreeMap;
    }

    public void setSkilltreeMap(Map<Integer, String> raceSkilltreeMap) {
        this.raceSkilltreeMap = raceSkilltreeMap;
    }

    public Map<String, ItemStack> getRaceItems() {
        return raceItems;
    }

    public void setRaceItems(Map<String, ItemStack> raceItems) {
        this.raceItems = raceItems;
    }

    public RaceIconData getIconData() {
        return iconData;
    }

    public void setIconData(RaceIconData iconData) {
        this.iconData = iconData;
    }

    public Map<String, List<RacePermission>> getRacePermissionsMap() {
        return racePermissionsMap;
    }

    public void setRacePermissionsMap(Map<String, List<RacePermission>> racePermissionsMap) {
        this.racePermissionsMap = racePermissionsMap;
    }

    public Map<String, List<RaceAttribute>> getRaceAttributesMap() {
        return raceAttributesMap;
    }

    public void setRaceAttributesMap(Map<String, List<RaceAttribute>> raceAttributesMap) {
        this.raceAttributesMap = raceAttributesMap;
    }

    public Map<Integer, Integer> getRaceLevelMap() {
        return raceLevelMap;
    }

    public void setRaceLevelMap(Map<Integer, Integer> raceLevelMap) {
        this.raceLevelMap = raceLevelMap;
    }

    public Map<Integer, Integer> getRaceSkillpointsMap() {
        return raceSkillpointMap;
    }

    public void setRaceSkillpointsMap(Map<Integer, Integer> raceSkillpointsMap) {
        this.raceSkillpointMap = raceSkillpointsMap;
    }

    public Map<String, List<RaceTrigger>> getRaceTriggersMap() {
        return raceTriggersMap;
    }

    public void setRaceTriggersMap(Map<String, List<RaceTrigger>> raceTriggersMap) {
        this.raceTriggersMap = raceTriggersMap;
    }

    public Map<String, List<RaceAbility>> getRaceAbilitiesMap() {
        return raceAbilitiesMap;
    }

    public void setRaceAbilitiesMap(Map<String, List<RaceAbility>> raceAbilitiesMap) {
        this.raceAbilitiesMap = raceAbilitiesMap;
    }

    public boolean isMaxLevel(int level) {
        if (raceLevelMap.isEmpty())
            return true;

        if (raceLevelMap.size() < level)
            return true;

        return false;
    }

    public int getRequiredExperience(int level) {
        if (!raceLevelMap.containsKey(level))
            return 0;

        return raceLevelMap.get(level);
    }

    public int getSkillpointsForLevel(int level) {
        if (raceSkillpointMap.containsKey(level))
            return raceSkillpointMap.get(level);

        return 0;
    }

    // public RaceCommandExecutor getExecutor() {
    // 	return executor;
    // }

    // public void setExecutor(RaceCommandExecutor executor) {
    //	this.executor = executor;
    // }

    public FileConfiguration getConfig() {
        return raceConfig;
    }
}
