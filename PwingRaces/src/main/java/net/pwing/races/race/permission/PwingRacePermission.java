package net.pwing.races.race.permission;

import net.pwing.races.api.race.permission.RacePermission;

public class PwingRacePermission implements RacePermission {

    private String node;
    private String requirement;

    public PwingRacePermission(String node, String requirement) {
        this.node = node;
        this.requirement = requirement;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
