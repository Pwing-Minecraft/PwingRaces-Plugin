package net.pwing.races.race.attribute;

import lombok.Getter;
import lombok.Setter;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.attribute.RaceAttribute;
import net.pwing.races.api.util.math.EquationOperator;
import net.pwing.races.api.util.math.EquationResult;
import net.pwing.races.util.math.EquationUtil;

import org.bukkit.Bukkit;

import javax.script.ScriptException;

@Getter
@Setter
public class PwingRaceAttribute implements RaceAttribute {

    private static String PLACEHOLDER_PATTERN = "%.*?%";

    private String attribute;
    private String attributeData;
    private String requirement;
    private EquationResult equationResult;

    public PwingRaceAttribute(String attribute, String attributeData, String requirement) {
        this.attribute = attribute;
        this.attributeData = attributeData;
        this.requirement = requirement;

        // PlaceholderAPI data has to be determined
        try {
            this.equationResult = EquationUtil.getEquationResult(attributeData.replaceAll(PLACEHOLDER_PATTERN, ""));
        } catch (ScriptException ex) {
            // Might error out if loaded, so ignore. We redo the equation when the attribute is applied
            if (!PwingRaces.getInstance().isPlaceholderAPILoaded()) {
                Bukkit.getLogger().warning("Attribute " + attribute + " failed to determine equation " + attributeData + ".");
                ex.printStackTrace();
            }
            this.equationResult = new EquationResult(EquationOperator.ADD, 0);
        }
    }
}
