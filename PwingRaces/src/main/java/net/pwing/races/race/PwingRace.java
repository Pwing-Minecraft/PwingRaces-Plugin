package net.pwing.races.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.ability.RaceAbility;
import net.pwing.races.api.race.ability.RaceAbilityManager;
import net.pwing.races.race.attribute.RaceAttribute;
import net.pwing.races.race.permission.RacePermission;
import net.pwing.races.race.skilltree.RaceSkilltree;
import net.pwing.races.race.trigger.RaceTrigger;
import net.pwing.races.utilities.AttributeUtil;
import net.pwing.races.utilities.ItemUtil;
import net.pwing.races.utilities.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class PwingRace implements Race {

    private String name;
    private int maxLevel;

    private Location spawnLocation;

    private boolean requireUnlock = false;

    private YamlConfiguration raceConfig;

    private ItemStack iconUnlocked;
    private ItemStack iconSelected;
    private ItemStack iconLocked;
    private int iconSlot;

    private Map<String, ItemStack> raceItems;

    private Map<Integer, String> raceSkilltreeMap;
    private Map<String, List<RacePermission>> racePermissionsMap;
    private Map<String, List<RaceAttribute>> raceAttributesMap;
    private Map<Integer, Integer> raceLevelMap;
    private Map<Integer, Integer> raceSkillpointMap;

    private Map<String, List<RaceTrigger>> raceTriggersMap;
    private Map<String, List<RaceAbility>> raceAbilitiesMap;

    // private RaceCommandExecutor executor;

    public PwingRace(PwingRaceManager raceManager, YamlConfiguration raceConfig) {
        loadDataFromConfig(raceManager, raceConfig);

        // this.executor = new IndividualRaceExecutor(raceManager.getPlugin(), this, name.toLowerCase());
    }

    public void loadDataFromConfig(PwingRaceManager raceManager, YamlConfiguration raceConfig) {
        this.raceConfig = raceConfig;

        this.name = raceConfig.getString("race.name");
        this.maxLevel = raceConfig.getInt("race.max-level");

        this.spawnLocation = LocationUtil.fromString(raceConfig.getString("race.spawn-location", ""));

        this.requireUnlock = raceConfig.getBoolean("race.require-unlock", false);
        this.raceSkilltreeMap = new HashMap<Integer, String>();

        this.raceItems = new HashMap<String, ItemStack>();
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

            raceSkilltreeMap.put(Integer.parseInt(str.split(" ")[1]), skilltree.getRegName());
        }

        this.raceLevelMap = new HashMap<Integer, Integer>();
        this.raceSkillpointMap = new HashMap<Integer, Integer>();

        if (raceConfig.contains("race.levels")) {
            for (String str : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                raceLevelMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".xp", 0));
                raceSkillpointMap.put(Integer.parseInt(str), raceConfig.getInt("race.levels." + str + ".skillpoints", 0));
            }
        }

        this.raceTriggersMap = new HashMap<String, List<RaceTrigger>>();

        // Get triggers defined in the triggers section
        if (raceConfig.contains("race.triggers")) {
            for (String str : raceConfig.getConfigurationSection("race.triggers").getKeys(false)) {
                List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(str, new ArrayList<RaceTrigger>());
                raceTriggers.add(new RaceTrigger(str, "race.triggers." + str, raceConfig, "none"));
                raceTriggersMap.put(str, raceTriggers);
            }
        }

        // Get triggers defined in the elements section
        if (raceConfig.contains("race.elements")) {
            for (String elem : raceConfig.getConfigurationSection("race.elements").getKeys(false)) {
                if (!raceConfig.contains("race.elements." + elem + ".triggers"))
                    continue;

                for (String trigger : raceConfig.getConfigurationSection("race.elements." + elem + ".triggers").getKeys(false)) {
                    List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<RaceTrigger>());
                    raceTriggers.add(new RaceTrigger(trigger, "race.elements." + elem + ".triggers." + trigger, raceConfig, elem));
                    raceTriggersMap.put(trigger, raceTriggers);
                }
            }
        }

        // Get triggers defined in the level section
        if (raceConfig.contains("race.levels")) {
            for (String level : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                if (!raceConfig.contains("race.levels." + level + ".triggers"))
                    continue;

                for (String trigger : raceConfig.getConfigurationSection("race.levels." + level + ".triggers").getKeys(false)) {
                    List<RaceTrigger> raceTriggers = raceTriggersMap.getOrDefault(trigger, new ArrayList<RaceTrigger>());
                    raceTriggers.add(new RaceTrigger(trigger, "race.levels." + level + ".triggers." + trigger, raceConfig, "level" + level));
                    raceTriggersMap.put(trigger, raceTriggers);
                }
            }
        }

        this.raceAttributesMap = new HashMap<String, List<RaceAttribute>>();

        // Get attributes defined in the attributes section
        if (raceConfig.contains("race.attributes")) {
            for (String str : raceConfig.getConfigurationSection("race.attributes").getKeys(false)) {
                List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(str, new ArrayList<RaceAttribute>());
                raceAttributes.add(new RaceAttribute(AttributeUtil.getAttributeName(str), raceConfig.getDouble("race.attributes." + str), "none"));
                raceAttributesMap.put(str, raceAttributes);
            }
        }

        // Get attributes defined in the elements section
        if (raceConfig.contains("race.elements")) {
            for (String elem : raceConfig.getConfigurationSection("race.elements").getKeys(false)) {
                if (!raceConfig.contains("race.elements." + elem + ".attributes"))
                    continue;

                for (String attribute : raceConfig.getConfigurationSection("race.elements." + elem + ".attributes").getKeys(false)) {
                    List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<RaceAttribute>());
                    raceAttributes.add(new RaceAttribute(AttributeUtil.getAttributeName(attribute), raceConfig.getDouble("race.elements." + elem + ".attributes." + attribute), elem));
                    raceAttributesMap.put(attribute, raceAttributes);
                }
            }
        }

        // Get attributes defined in the level section
        if (raceConfig.contains("race.levels")) {
            for (String level : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                if (!raceConfig.contains("race.levels." + level + ".attributes"))
                    continue;

                for (String attribute : raceConfig.getConfigurationSection("race.levels." + level + ".attributes").getKeys(false)) {
                    List<RaceAttribute> raceAttributes = raceAttributesMap.getOrDefault(attribute, new ArrayList<RaceAttribute>());
                    raceAttributes.add(new RaceAttribute(AttributeUtil.getAttributeName(attribute), raceConfig.getDouble("race.levels." + level + ".attributes." + attribute), "level" + level));
                    raceAttributesMap.put(attribute, raceAttributes);
                }
            }
        }

        this.racePermissionsMap = new HashMap<String, List<RacePermission>>();

        // Get permissions defined in the permissions section
        if (raceConfig.contains("race.permissions")) {
            for (String str : raceConfig.getStringList("race.permissions")) {
                List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(str, new ArrayList<RacePermission>());
                racePermissions.add(new RacePermission(str, "none"));
                racePermissionsMap.put(str, racePermissions);
            }
        }

        // Get permissions defined in the elements section
        if (raceConfig.contains("race.elements")) {
            for (String elem : raceConfig.getConfigurationSection("race.elements").getKeys(false)) {
                if (!raceConfig.contains("race.elements." + elem + ".permissions"))
                    continue;

                for (String permission : raceConfig.getStringList("race.elements." + elem + ".permissions")) {
                    List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<RacePermission>());
                    racePermissions.add(new RacePermission(permission, elem));
                    racePermissionsMap.put(permission, racePermissions);
                }
            }
        }

        // Get permissions defined in the level section
        if (raceConfig.contains("race.levels")) {
            for (String level : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                if (!raceConfig.contains("race.levels," + level + ".permissions"))
                    continue;

                for (String permission : raceConfig.getStringList("race.levels." + level + ".permissions")) {
                    List<RacePermission> racePermissions = racePermissionsMap.getOrDefault(permission, new ArrayList<RacePermission>());
                    racePermissions.add(new RacePermission(permission, "level" + level));
                    racePermissionsMap.put(permission, racePermissions);
                }
            }
        }

        this.raceAbilitiesMap = new HashMap<String, List<RaceAbility>>();
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

        // Get abilities defined in the elements section
        if (raceConfig.contains("race.elements")) {
            for (String elem : raceConfig.getConfigurationSection("race.elements").getKeys(false)) {
                if (!raceConfig.contains("race.elements." + elem + ".abilities"))
                    continue;

                for (String ability : raceConfig.getConfigurationSection("race.elements." + elem + ".abilities").getKeys(false)) {
                    List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<RaceAbility>());
                    RaceAbility raceAbility = abilityManager.getAbility(ability, elem, "race.elements." + elem + ".abilities." + ability, raceConfig);
                    if (raceAbility != null)
                        raceAbilities.add(raceAbility);

                    raceAbilitiesMap.put(ability, raceAbilities);
                }
            }
        }

        // Get abilities defined in the level section
        if (raceConfig.contains("race.levels")) {
            for (String level : raceConfig.getConfigurationSection("race.levels").getKeys(false)) {
                if (!raceConfig.contains("race.levels." + level + ".abilities"))
                    continue;

                for (String ability : raceConfig.getConfigurationSection("race.levels." + level + ".abilities").getKeys(false)) {
                    List<RaceAbility> raceAbilities = raceAbilitiesMap.getOrDefault(ability, new ArrayList<RaceAbility>());
                    RaceAbility raceAbility = abilityManager.getAbility(ability, "level" + level, "race.levels." + level + ".abilities." + ability, raceConfig);
                    if (raceAbility != null)
                        raceAbilities.add(raceAbility);

                    raceAbilitiesMap.put(ability, raceAbilities);
                }
            }
        }

        this.iconUnlocked = ItemUtil.readItemFromConfig("race.gui.icon", raceConfig);
        if (iconUnlocked == null) {
            Bukkit.getLogger().warning("[PwingRaces] " + "Could not load icon for race " + name + ". Please make sure your config is correct!");
            return;
        }

        if (raceConfig.contains("race.gui.icon-selected"))
            this.iconSelected = ItemUtil.readItemFromConfig("race.gui.icon-selected", raceConfig);
        else
            this.iconSelected = iconUnlocked;

        if (raceConfig.contains("race.gui.icon-locked"))
            this.iconLocked = ItemUtil.readItemFromConfig("race.gui.icon-locked", raceConfig);
        else
            this.iconLocked = iconUnlocked;

        this.iconSlot = raceConfig.getInt("race.gui.slot", 0);
    }

    public String getName() {
        return name;
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

    public ItemStack getUnlockedIcon() {
        return iconUnlocked;
    }

    public void setUnlockedIcon(ItemStack iconUnlocked) {
        this.iconUnlocked = iconUnlocked;
    }

    public ItemStack getSelectedIcon() {
        return iconSelected;
    }

    public void setSelectedIcon(ItemStack iconSelected) {
        this.iconSelected = iconSelected;
    }

    public ItemStack getLockedIcon() {
        return iconLocked;
    }

    public void setLockedIcon(ItemStack iconLocked) {
        this.iconLocked = iconLocked;
    }

    public int getIconSlot() {
        return iconSlot;
    }

    public void setIconSlot(int iconSlot) {
        this.iconSlot = iconSlot;
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

    public YamlConfiguration getConfig() {
        return raceConfig;
    }
}
