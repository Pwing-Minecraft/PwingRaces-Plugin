package net.pwing.races.race.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import net.pwing.races.api.race.permission.RacePermission;

@Getter
@Setter
@AllArgsConstructor
public class PwingRacePermission implements RacePermission {

    private String node;
    private String requirement;
}
