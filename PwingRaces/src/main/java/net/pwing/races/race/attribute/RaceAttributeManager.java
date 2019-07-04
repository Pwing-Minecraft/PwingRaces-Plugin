package net.pwing.races.race.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.race.attribute.attributes.ManaAttribute;
import net.pwing.races.utilities.AttributeUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RaceAttributeManager {

    private PwingRaces plugin;

    private Map<String, RaceAttributeEffect> attributeEffects = new HashMap<String, RaceAttributeEffect>();

    public RaceAttributeManager(PwingRaces plugin) {
        this.plugin = plugin;

        initAttributeEffects();
        Bukkit.getServer().getPluginManager().registerEvents(new RaceAttributeListener(plugin), plugin);
    }

    public void initAttributeEffects() {
        attributeEffects.put("max-mana", new ManaAttribute(plugin, "max-mana"));
    }

    public void applyAttributeBonuses(Player player) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld())) {
            for (RaceAttribute definedAttribute : getApplicableAttributes(player)) {
                if (AttributeUtil.isBukkitAttribute(definedAttribute.getAttribute())) {
                    double def = AttributeUtil.getDefaultAttributeValue(player, definedAttribute.getAttribute());
                    AttributeUtil.setAttributeValue(player, definedAttribute.getAttribute(), def);
                }

                if (attributeEffects.containsKey(definedAttribute.getAttribute()))
                    attributeEffects.get(definedAttribute.getAttribute()).onAttributeLose(player);
            }

            return;
        }

        for (RaceAttribute definedAttribute : getApplicableAttributes(player)) {
            if (AttributeUtil.isBukkitAttribute(definedAttribute.getAttribute()))
                AttributeUtil.setAttributeValue(player, definedAttribute.getAttribute(), definedAttribute.getValue());

            if (attributeEffects.containsKey(definedAttribute.getAttribute()))
                attributeEffects.get(definedAttribute.getAttribute()).onAttributeApply(player, definedAttribute.getValue());
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
            return new ArrayList<RaceAttribute>();

        Race race = racePlayer.getActiveRace();
        if (race == null)
            return new ArrayList<RaceAttribute>();

        RaceData data = raceManager.getPlayerData(player, race);
        List<RaceAttribute> attributes = new ArrayList<RaceAttribute>();
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
