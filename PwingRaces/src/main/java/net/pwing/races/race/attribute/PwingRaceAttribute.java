package net.pwing.races.race.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.attribute.RaceAttribute;

@Getter
@Setter
@AllArgsConstructor
public class PwingRaceAttribute implements RaceAttribute {

    private String attribute;
    private double value;
    private String requirement;
}
