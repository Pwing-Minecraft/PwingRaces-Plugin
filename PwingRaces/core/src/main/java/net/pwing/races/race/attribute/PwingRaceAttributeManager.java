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
import net.pwing.races.race.attribute.attributes.FlySpeedAttribute;
import net.pwing.races.race.attribute.attributes.ManaAttribute;
import net.pwing.races.race.attribute.attributes.WalkSpeedAttribute;
import net.pwing.races.race.attribute.attributes.WisdomAttribute;
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

    private void initAttributeEffects() {
        attributeEffects.put("fly-speed", new FlySpeedAttribute("fly-speed"));
        attributeEffects.put("max-mana", new ManaAttribute(plugin, "max-mana"));
        attributeEffects.put("walk-speed", new WalkSpeedAttribute("walk-speed"));
        attributeEffects.put("wisdom", new WisdomAttribute(plugin, "wisdom"));
    }

    @Override
    public void applyAttributeBonuses(Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            return;
        }
        Map<String, List<RaceAttribute>> bundledAttributes = new HashMap<>();
        for (RaceAttribute attribute : getApplicableAttributes(player)) {
            List<RaceAttribute> attributes = bundledAttributes.getOrDefault(attribute.getAttribute(), new ArrayList<>());
            attributes.add(attribute);
            bundledAttributes.put(attribute.getAttribute(), attributes);
        }

        for (Map.Entry<String, List<RaceAttribute>> attributeEntry : bundledAttributes.entrySet()) {
            double value = 0;
            boolean bukkitAttribute = false;
            if (AttributeUtil.isBukkitAttribute(attributeEntry.getKey())) {
                bukkitAttribute = true;
                value = AttributeUtil.getDefaultAttributeValue(player, attributeEntry.getKey());
            }

            for (RaceAttribute attribute : attributeEntry.getValue()) {
                value = EquationUtil.getValue(value, EquationUtil.getEquationResult(player, attribute.getAttributeData()));
            }

            if (racePlayer.getTemporaryAttributes().containsKey(attributeEntry.getKey())) {
                value = EquationUtil.getValue(value, racePlayer.getTemporaryAttributes().get(attributeEntry.getKey()));
            }

            if (attributeEffects.containsKey(attributeEntry.getKey()))
                attributeEffects.get(attributeEntry.getKey()).onAttributeApply(player, value);

            if (bukkitAttribute)
                AttributeUtil.setAttributeValue(player, attributeEntry.getKey(), value);
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

    public double getAttributeBonus(Player player, String attributeStr) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null) {
            return 0;
        }

        double value = 0;
        for (RaceAttribute attribute : getApplicableAttributes(player)) {
            if (!attribute.getAttribute().equalsIgnoreCase(attributeStr))
                continue;

            value = EquationUtil.getValue(value, EquationUtil.getEquationResult(player, attribute.getAttributeData()));
        }

        if (racePlayer.getTemporaryAttributes().containsKey(attributeStr)) {
            value = EquationUtil.getValue(value, racePlayer.getTemporaryAttributes().get(attributeStr));
        }
        return value;
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
        for (Map.Entry<String, List<RaceAttribute>> entry : race.getRaceAttributesMap().entrySet()) {
            List<RaceAttribute> definedAttributes = entry.getValue();

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
