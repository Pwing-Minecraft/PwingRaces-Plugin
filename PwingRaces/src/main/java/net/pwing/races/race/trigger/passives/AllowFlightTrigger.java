package net.pwing.races.race.trigger.passives;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import org.bukkit.entity.Player;

public class AllowFlightTrigger extends RaceTriggerPassive {

    private PwingRaces plugin;

    public AllowFlightTrigger(PwingRaces plugin, String name) {
        super(name);

        this.plugin = plugin;
    }

    @Override
    public void runTriggerPassive(Player player, String trigger) {
        String[] split = trigger.split(" ");
        if (split.length < 2)
            return;

        if (Boolean.parseBoolean(split[1]))
            player.setAllowFlight(true);
        else
            player.setAllowFlight(false);
    }
}
