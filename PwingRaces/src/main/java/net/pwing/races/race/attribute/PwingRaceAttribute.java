package net.pwing.races.race.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.attribute.RaceAttribute;
import net.pwing.races.api.util.math.EquationResult;

@Getter
@AllArgsConstructor
public class PwingRaceAttribute implements RaceAttribute {

    @Setter
    private String attribute;
    private EquationResult equationResult;
    private String requirement;
}
