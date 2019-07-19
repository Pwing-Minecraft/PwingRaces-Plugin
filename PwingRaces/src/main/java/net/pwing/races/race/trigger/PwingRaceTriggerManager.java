package net.pwing.races.race.trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.trigger.RaceTrigger;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.race.trigger.passives.AddPotionEffectTrigger;
import net.pwing.races.race.trigger.passives.BurnTrigger;
import net.pwing.races.race.trigger.passives.DropItemTrigger;
import net.pwing.races.race.trigger.passives.GiveHealthTrigger;
import net.pwing.races.race.trigger.passives.RunCommandTrigger;
import net.pwing.races.race.trigger.passives.SendMessageTrigger;
import net.pwing.races.race.trigger.passives.ToggleFlyTrigger;
import net.pwing.races.race.trigger.passives.DisguiseTrigger;
import net.pwing.races.race.trigger.passives.GiveExpTrigger;
import net.pwing.races.race.trigger.passives.GiveRaceExpTrigger;
import net.pwing.races.race.trigger.passives.GiveSaturationTrigger;
import net.pwing.races.race.trigger.passives.RemovePotionEffectTrigger;
import net.pwing.races.race.trigger.passives.UndisguiseTrigger;
import net.pwing.races.utilities.NumberUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PwingRaceTriggerManager implements RaceTriggerManager {

    private PwingRaces plugin;

    private Map<String, Map<UUID, Long>> delay = new HashMap<String, Map<UUID, Long>>();
    private Map<String, RaceTriggerPassive> passives = new HashMap<String, RaceTriggerPassive>();

    public PwingRaceTriggerManager(PwingRaces plugin) {
        this.plugin = plugin;

        initTriggerPassives();
        Bukkit.getServer().getPluginManager().registerEvents(new RaceTriggerListener(plugin), plugin);
    }

    public void initTriggerPassives() {
        passives.put("add-potion-effect", new AddPotionEffectTrigger(plugin, "add-potion-effect"));
        passives.put("burn", new BurnTrigger(plugin, "burn"));
        passives.put("disguise", new DisguiseTrigger(plugin, "disguise"));
        passives.put("drop-item", new DropItemTrigger(plugin, "drop-item"));
        passives.put("give-exp", new GiveExpTrigger(plugin, "give-exp"));
        passives.put("give-health", new GiveHealthTrigger(plugin, "give-health"));
        passives.put("give-race-exp", new GiveRaceExpTrigger(plugin, "give-race-exp"));
        passives.put("give-saturation", new GiveSaturationTrigger(plugin, "give-saturation"));
        passives.put("remove-potion-effect", new RemovePotionEffectTrigger(plugin, "remove-potion-effect"));
        passives.put("run-command", new RunCommandTrigger(plugin, "run-command"));
        passives.put("send-message", new SendMessageTrigger(plugin, "send-message"));
        passives.put("toggle-fly", new ToggleFlyTrigger(plugin, "toggle-fly"));
        passives.put("undisguise", new UndisguiseTrigger(plugin, "undisguise"));
    }

    public void runTriggers(Player player, String trigger) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld()))
            return;

        Random random = new Random();
        Collection<RaceTrigger> raceTriggers = getApplicableTriggers(player, trigger);
        if (raceTriggers == null || raceTriggers.isEmpty())
            return;

        for (RaceTrigger raceTrigger : raceTriggers) {
            if (hasDelay(player, raceTrigger.getInternalName()))
                continue;

            setDelay(player, raceTrigger.getInternalName(), raceTrigger.getDelay());

            // Run chance afterward so it doesnt idle
            if ((random.nextFloat() * 100) > raceTrigger.getChance())
                continue;

            runTriggerPassives(player, raceTrigger);
        }
    }

    public void runTaskTriggers(Player player, String trigger, int tick) {
        if (!trigger.startsWith("ticks "))
            return;

        Random random = new Random();
        for (RaceTrigger raceTrigger : getApplicableTaskTriggers(player)) {
            if (hasDelay(player, raceTrigger.getInternalName())) {
                continue;
            }

            if (!NumberUtil.isInteger(raceTrigger.getTrigger().split(" ")[1])) {
                plugin.getLogger().warning("Could not properly parse trigger " + raceTrigger.getTrigger() + ", expected a number but got " + trigger.split(" ")[1] + "");
                continue;
            }

            int tickDelay = Integer.parseInt(raceTrigger.getTrigger().split(" ")[1]);
            if (tick % tickDelay == 0) {
                setDelay(player, raceTrigger.getInternalName(), raceTrigger.getDelay());

                // Run chance afterward so it doesnt idle
                if ((random.nextFloat() * 100) > raceTrigger.getChance())
                    continue;

                Bukkit.getScheduler().runTask(plugin, () -> {
                    runTriggerPassives(player, raceTrigger);
                });
            }
        }
    }

    public Collection<RaceTrigger> getApplicableTriggers(Player player, String trigger) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<RaceTrigger>();

        Race race = racePlayer.getActiveRace();
        if (race == null)
            return new ArrayList<RaceTrigger>();

        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceTrigger> triggers = new HashMap<String, RaceTrigger>();
        for (String key : race.getRaceTriggersMap().keySet()) {
            List<RaceTrigger> definedTriggers = race.getRaceTriggersMap().get(key);

            for (RaceTrigger definedTrigger : definedTriggers) {
                String req = definedTrigger.getRequirement();

                if (req.equals("none")) {
                    triggers.put(definedTrigger.getInternalName(), definedTrigger);

                } else if (req.startsWith("level")) { // best to assume it's a level-based trigger
                    int level = Integer.parseInt(req.replace("level", ""));

                    if (data.getLevel() < level)
                        continue;

                    triggers.put(definedTrigger.getInternalName(), definedTrigger);
                } else {
                    for (RaceSkilltree skillTree : raceManager.getSkilltreeManager().getSkilltrees()) {
                        if (data.hasPurchasedElement(skillTree.getInternalName(), req)) {
                            triggers.put(definedTrigger.getInternalName(), definedTrigger);
                        }
                    }
                }
            }
        }

        // Remove triggers that may be overridden
        List<String> toRemove = new ArrayList<String>();
        for (String str : triggers.keySet()) {
            RaceTrigger raceTrigger = triggers.get(str);

            if (!raceTrigger.getTrigger().equalsIgnoreCase(trigger)) {
                toRemove.add(str);
            }
        }

        toRemove.forEach(removed -> triggers.remove(removed));
        return triggers.values();
    }

    public Collection<RaceTrigger> getApplicableTaskTriggers(Player player) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<RaceTrigger>();

        Race race = racePlayer.getActiveRace();
        if (race == null)
            return new ArrayList<RaceTrigger>();

        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceTrigger> triggers = new HashMap<String, RaceTrigger>();
        for (String key : race.getRaceTriggersMap().keySet()) {
            List<RaceTrigger> definedTriggers = race.getRaceTriggersMap().get(key);

            for (RaceTrigger definedTrigger : definedTriggers) {
                String req = definedTrigger.getRequirement();

                if (req.equals("none")) {
                    triggers.put(definedTrigger.getInternalName(), definedTrigger);

                } else if (req.startsWith("level")) { // best to assume it's a level-based trigger
                    int level = Integer.parseInt(req.replace("level", ""));

                    if (data.getLevel() < level)
                        continue;

                    triggers.put(definedTrigger.getInternalName(), definedTrigger);
                } else {
                    for (RaceSkilltree skillTree : raceManager.getSkilltreeManager().getSkilltrees()) {
                        if (data.hasPurchasedElement(skillTree.getInternalName(), req)) {
                            triggers.put(definedTrigger.getInternalName(), definedTrigger);
                        }
                    }
                }
            }
        }

        // Remove triggers that may be overridden
        List<String> toRemove = new ArrayList<String>();
        for (String str : triggers.keySet()) {
            RaceTrigger raceTrigger = triggers.get(str);

            if (!raceTrigger.getTrigger().startsWith("ticks ")) {
                toRemove.add(str);
            }
        }

        toRemove.forEach(removed -> triggers.remove(removed));
        return triggers.values();
    }

    public void runTriggerPassives(Player player, RaceTrigger trigger) {
        runTriggerPassives(player, trigger.getPassives());
    }

    public void runTriggerPassives(Player player, List<String> triggers) {
        for (String effect : triggers) {
            String name = effect.split(" ")[0];
            RaceTriggerPassive racePassive = passives.get(name);
            if (racePassive == null)
                continue;

            racePassive.runPassive(player, effect);
        }
    }

    public boolean hasDelay(Player player, String trigger) {
        if (!delay.containsKey(trigger))
            return false;

        if (!delay.get(trigger).containsKey(player.getUniqueId()))
            return false;

        if (delay.get(trigger).get(player.getUniqueId()) < System.currentTimeMillis())
            return false;

        return true;
    }

    public void setDelay(Player player, String trigger, int amt) {
        Map<UUID, Long> delayMap = new HashMap<UUID, Long>();

        if (delay.containsKey(trigger))
            delayMap = delay.get(trigger);

        long time = (amt * 1000) + System.currentTimeMillis();

        delayMap.put(player.getUniqueId(), time);
        delay.put(trigger, delayMap);
    }

    public Map<String, RaceTriggerPassive> getTriggerPassives() {
        return passives;
    }
}
