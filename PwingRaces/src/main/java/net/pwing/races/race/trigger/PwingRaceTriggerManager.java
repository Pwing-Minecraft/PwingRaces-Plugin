package net.pwing.races.race.trigger;

import lombok.AccessLevel;
import lombok.Getter;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.trigger.RaceTrigger;
import net.pwing.races.api.race.trigger.RaceTriggerManager;
import net.pwing.races.api.race.trigger.RaceTriggerPassive;
import net.pwing.races.api.race.trigger.condition.RaceCondition;
import net.pwing.races.race.trigger.conditions.*;
import net.pwing.races.race.trigger.passives.*;
import net.pwing.races.race.trigger.triggers.*;
import net.pwing.races.race.trigger.triggers.holder.EnvironmentTriggerHolder;
import net.pwing.races.util.math.NumberUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class PwingRaceTriggerManager implements RaceTriggerManager {

    @Getter(AccessLevel.NONE)
    private PwingRaces plugin;

    private Map<String, RaceTriggerPassive> triggerPassives = new HashMap<>();
    private Map<String, RaceCondition> conditions = new HashMap<>();

    private Map<String, Map<UUID, Long>> delay = new HashMap<>();

    public PwingRaceTriggerManager(PwingRaces plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskLater(plugin, this::initTriggers, 100);
    }

    private void initTriggers() {
        triggerPassives.put("add-potion-effect", new AddPotionEffectTriggerPassive(plugin, "add-potion-effect"));
        triggerPassives.put("allow-flight", new AllowFlightTriggerPassive(plugin, "allow-flight"));
        triggerPassives.put("burn", new BurnTriggerPassive(plugin, "burn"));
        triggerPassives.put("damage", new DamageTriggerPassive(plugin, "damage"));
        triggerPassives.put("disguise", new DisguiseTriggerPassive(plugin, "disguise"));
        triggerPassives.put("drop-item", new DropItemTriggerPassive(plugin, "drop-item"));
        triggerPassives.put("give-exp", new GiveExpTriggerPassive(plugin, "give-exp"));
        triggerPassives.put("give-health", new GiveHealthTriggerPassive(plugin, "give-health"));
        triggerPassives.put("give-race-exp", new GiveRaceExpTriggerPassive(plugin, "give-race-exp"));
        triggerPassives.put("give-saturation", new GiveSaturationTriggerPassive(plugin, "give-saturation"));
        triggerPassives.put("reapply-attributes", new ReapplyAttributesTriggerPassive(plugin, "reapply-attributes"));
        triggerPassives.put("remove-potion-effect", new RemovePotionEffectTriggerPassive(plugin, "remove-potion-effect"));
        triggerPassives.put("run-command", new RunCommandTriggerPassive(plugin, "run-command"));
        triggerPassives.put("send-actionbar-message", new SendActionBarMessageTriggerPassive("send-action-bar-message"));
        triggerPassives.put("send-message", new SendMessageTriggerPassive(plugin, "send-message"));
        triggerPassives.put("set-attribute", new SetAttributeTriggerPassive(plugin, "set-attribute"));
        triggerPassives.put("set-default-attributes", new SetDefaultAttributesTriggerPassive(plugin, "set-default-attributes"));
        triggerPassives.put("toggle-fly", new ToggleFlyTriggerPassive(plugin, "toggle-fly"));
        triggerPassives.put("undisguise", new UndisguiseTriggerPassive(plugin, "undisguise"));

        // Register dual conditions/triggers
        registerCondition("burn", new BurnTrigger(this));
        registerCondition("fly", new FlyTrigger(this));
        registerCondition("in-region", new InRegionTrigger(plugin));
        registerCondition("sneak", new SneakTrigger(this));
        registerCondition("ticks", new TicksTrigger(plugin));

        registerCondition("block-relative", new BlockRelativeCondition());
        registerCondition("day", new DayCondition());
        registerCondition("disguised", new DisguisedCondition(plugin));
        registerCondition("in-biome", new InBiomeCondition());
        registerCondition("in-moonlight", new InMoonlightCondition());
        registerCondition("inside", new InsideCondition());
        registerCondition("in-sunlight", new InSunlightCondition());
        registerCondition("in-world", new InWorldCondition());
        registerCondition("near-race", new NearRaceCondition(plugin));
        registerCondition("moon-phase", new MoonPhaseCondition());
        registerCondition("night", new NightCondition());
        registerCondition("outside", new OutsideCondition());

        registerTrigger(new BlockBreakTrigger(this));
        registerTrigger(new BlockPlaceTrigger(this));
        registerTrigger(new BreedAnimalTrigger(this));
        registerTrigger(new ConsumeItemTrigger(this));
        registerTrigger(new CraftItemTrigger(this));
        registerTrigger(new DamageEntityTrigger(plugin));
        registerTrigger(new DeathTrigger(plugin));
        registerTrigger(new EnchantItemTrigger(this));
        registerTrigger(new FishTrigger(this));
        registerTrigger(new HealthRegenTrigger(this));
        registerTrigger(new JoinTrigger(this));
        registerTrigger(new KillEntityTrigger(plugin));
        registerTrigger(new LaunchProjectileTrigger(this));
        registerTrigger(new MoveTrigger(this));
        registerTrigger(new QuitTrigger(this));
        registerTrigger(new RaceChangeTrigger(this));
        registerTrigger(new RaceElementPurchaseTrigger(this));
        registerTrigger(new RaceExpChangeTrigger(this));
        registerTrigger(new RaceLevelUpTrigger(this));
        registerTrigger(new RaceReclaimItemsTrigger(this));
        registerTrigger(new RaceReclaimSkillpointsTrigger(this));
        registerTrigger(new TakeDamageTrigger(this));
        registerTrigger(new TameAnimalTrigger(this));
        registerTrigger(new TeleportTrigger(this));
        registerTrigger(new UseInventoryTrigger(this));

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EnvironmentTriggerHolder(this), 1, 1);
    }

    public void runTriggers(Player player, String trigger) {
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld()))
            return;

        triggerLoop:
        for (RaceTrigger raceTrigger : getApplicableTriggers(player, trigger)) {
            if (hasDelay(player, raceTrigger.getInternalName()))
                continue;

            for (String fullCondition : raceTrigger.getConditions().keySet()) {
                for (RaceCondition condition : raceTrigger.getConditions().get(fullCondition)) {
                    if (fullCondition.startsWith("!")) {
                        if (condition.check(player, fullCondition.substring(1).split(" ")))
                            continue triggerLoop;
                    } else {
                        if (!condition.check(player, fullCondition.split(" ")))
                            continue triggerLoop;
                    }
                }
            }

            setDelay(player, raceTrigger.getInternalName(), raceTrigger.getDelay());

            // Run chance afterward so it doesnt idle
            if ((NumberUtil.RANDOM.nextFloat() * 100) > raceTrigger.getChance())
                continue;

            // Run task synchronously
            Bukkit.getScheduler().runTask(plugin, () -> runTriggerPassives(player, raceTrigger));
        }
    }

    public void runTaskTriggers(Player player, String trigger, int tick) {
        if (!trigger.startsWith("ticks "))
            return;

        triggerLoop:
        for (RaceTrigger raceTrigger : getApplicableTaskTriggers(player)) {
            if (hasDelay(player, raceTrigger.getInternalName()))
                continue;

            for (String fullCondition : raceTrigger.getConditions().keySet()) {
                for (RaceCondition condition : raceTrigger.getConditions().get(fullCondition)) {
                    if (fullCondition.startsWith("!")) {
                        if (condition.check(player, fullCondition.substring(1).split(" ")))
                            continue triggerLoop;
                    } else {
                        if (!condition.check(player, fullCondition.split(" ")))
                            continue triggerLoop;
                    }
                }
            }

            int tickDelay = Integer.parseInt(raceTrigger.getTrigger().split(" ")[1]);
            if (tick % tickDelay == 0) {
                setDelay(player, raceTrigger.getInternalName(), raceTrigger.getDelay());

                // Run chance afterward so it doesnt idle
                if ((NumberUtil.RANDOM.nextFloat() * 100) > raceTrigger.getChance())
                    continue;

                // Run task synchronously
                Bukkit.getScheduler().runTask(plugin, () -> runTriggerPassives(player, raceTrigger));
            }
        }
    }

    public Collection<RaceTrigger> getApplicableTriggers(Player player, String trigger) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<>();

        if (!racePlayer.getRace().isPresent())
            return new ArrayList<>();

        Race race = racePlayer.getRace().get();
        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceTrigger> triggers = new HashMap<>();
        for (String key : race.getRaceTriggersMap().keySet()) {
            List<RaceTrigger> definedTriggers = race.getRaceTriggersMap().get(key);

            for (RaceTrigger definedTrigger : definedTriggers) {
                if (!definedTrigger.getTrigger().equalsIgnoreCase(trigger))
                    continue;

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
        return triggers.values();
    }

    public Collection<RaceTrigger> getApplicableTaskTriggers(Player player) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<>();

        if (!racePlayer.getRace().isPresent())
            return new ArrayList<>();

        Race race = racePlayer.getRace().get();
        RaceData data = raceManager.getPlayerData(player, race);

        Map<String, RaceTrigger> triggers = new HashMap<>();
        for (String key : race.getRaceTriggersMap().keySet()) {
            List<RaceTrigger> definedTriggers = race.getRaceTriggersMap().get(key);

            for (RaceTrigger definedTrigger : definedTriggers) {
                if (!definedTrigger.getTrigger().startsWith("ticks "))
                    continue;

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

        return triggers.values();
    }

    public void runTriggerPassives(Player player, RaceTrigger trigger) {
        trigger.getPassives().keySet().forEach(fullPassive -> trigger.getPassives().get(fullPassive).forEach(passive -> passive.runPassive(player, fullPassive)));
        // trigger.getPassives().forEach(passive -> passive.runTriggerPassive(player, trigger.getPassiveValue(passive).get().split(" ")));
    }

    public boolean hasDelay(Player player, String trigger) {
        if (!delay.containsKey(trigger))
            return false;

        if (!delay.get(trigger).containsKey(player.getUniqueId()))
            return false;

        return delay.get(trigger).get(player.getUniqueId()) >= System.currentTimeMillis();
    }

    public void setDelay(Player player, String trigger, int amt) {
        Map<UUID, Long> delayMap = delay.getOrDefault(trigger, new HashMap<>());
        delayMap.put(player.getUniqueId(), (amt * 1000) + System.currentTimeMillis());
        delay.put(trigger, delayMap);
    }

    private void registerCondition(String internalName, RaceCondition condition) {
        conditions.put(internalName, condition);
        if (condition instanceof Listener)
            plugin.getServer().getPluginManager().registerEvents((Listener) condition, plugin);
    }

    // this method may do more in the future, but it's just a registerEvents method for now
    private void registerTrigger(Object trigger) {
        if (trigger instanceof Listener)
            plugin.getServer().getPluginManager().registerEvents((Listener) trigger, plugin);
    }
}
