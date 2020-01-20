package net.pwing.races.race.attribute;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.attribute.RaceAttribute;
import net.pwing.races.api.race.attribute.RaceAttributeEffect;
import net.pwing.races.api.race.attribute.RaceAttributeManager;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.util.math.EquationResult;
import net.pwing.races.race.attribute.attributes.FlySpeedAttribute;
import net.pwing.races.race.attribute.attributes.ManaAttribute;
import net.pwing.races.race.attribute.attributes.WalkSpeedAttribute;
import net.pwing.races.util.AttributeUtil;

import net.pwing.races.util.math.EquationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PwingRaceAttributeManager implements RaceAttributeManager {

    private PwingRaces plugin;

    private Map<String, RaceAttributeEffect> attributeEffects = new HashMap<>();

    public PwingRaceAttributeManager(PwingRaces plugin) {
        this.plugin = plugin;

        initAttributeEffects();
        Bukkit.getServer().getPluginManager().registerEvents(new RaceAttributeListener(plugin), plugin);
    }

    public void initAttributeEffects() {
        attributeEffects.put("fly-speed", new FlySpeedAttribute("fly-speed"));
        attributeEffects.put("max-mana", new ManaAttribute(plugin, "max-mana"));
        attributeEffects.put("walk-speed", new WalkSpeedAttribute("walk-speed"));
    }

    @Override
    public void applyAttributeBonuses(Player player) {
        Map<String, List<RaceAttribute>> bundledAttributes = new HashMap<>();
        for (RaceAttribute attribute : getApplicableAttributes(player)) {
            bundledAttributes.put(attribute.getAttribute(), bundledAttributes.getOrDefault(attribute.getAttribute(), new ArrayList<>()));
        }

        for (Map.Entry<String, List<RaceAttribute>> attributeEntry : bundledAttributes.entrySet()) {
            double value = 0;
            if (AttributeUtil.isBukkitAttribute(attributeEntry.getKey()))
                value = AttributeUtil.getDefaultAttributeValue(player, attributeEntry.getKey());

            for (RaceAttribute attribute : attributeEntry.getValue()) {
                value = EquationUtil.getValue(value, attribute.getEquationResult());
            }

            if (attributeEffects.containsKey(attributeEntry.getKey()))
                attributeEffects.get(attributeEntry.getKey()).onAttributeApply(player, value);
        }
    }

    public void removeAttributeBonuses(Player player) {
        for (RaceAttribute definedAttribute : getApplicableAttributes(player)) {
            if (AttributeUtil.isBukkitAttribute(definedAttribute.getAttribute())) {
                double def = AttributeUtil.getDefaultAttributeValue(player, definedAttribute.getAttribute());
                AttributeUtil.setAttributeValue(player, definedAttribute.getAttribute(), def);
            }

            if (attributeEffects.containsKey(definedAttribute.getAttribute()))
                attributeEffects.get(definedAttribute.getAttribute()).onAttributeLose(player);
        }
    }

    public double getAttributeBonus(Player player, String attribute) {
        double bonus = 0;
        for (RaceAttribute definedAttribute : getApplicableAttributes(player)) {
            if (definedAttribute.getAttribute().replace("_", "-")
                    .equalsIgnoreCase(attribute))
                bonus = definedAttribute.getValue();
        }

        if (AttributeUtil.isBukkitAttribute(attribute))
            bonus = AttributeUtil.getAttributeValue(player, attribute);

        return bonus;
    }

    public Collection<RaceAttribute> getApplicableAttributes(Player player) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<>();

        if (!racePlayer.getRace().isPresent())
            return new ArrayList<>();

        Race race = racePlayer.getRace().get();
        RaceData data = raceManager.getPlayerData(player, race);
        List<RaceAttribute> attributes = new ArrayList<>();
        for (String key : race.getRaceAttributesMap().keySet()) {
            List<RaceAttribute> definedAttributes = race.getRaceAttributesMap().get(key);

            for (RaceAttribute definedAttribute : definedAttributes) {
                String req = definedAttribute.getRequirement();

                if (req.equals("none")) {
                    attributes.add(definedAttribute);

                } else if (req.startsWith("level")) { // best to assume it's a level-based attribute
                    int level = Integer.parseInt(req.replace("level", ""));

                    if (data.getLevel() < level)
                        continue;

                    attributes.add(definedAttribute);
                } else {
                    for (RaceSkilltree skillTree : raceManager.getSkilltreeManager().getSkilltrees()) {
                        if (data.hasPurchasedElement(skillTree.getInternalName(), req)) {
                            attributes.add(definedAttribute);
                        }
                    }
                }
            }
        }

        return attributes;
    }

    public Map<String, RaceAttributeEffect> getAttributeEffects() {
        return attributeEffects;
    }
}
