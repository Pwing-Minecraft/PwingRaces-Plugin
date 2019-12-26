package net.pwing.races.race.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.menu.RaceIconData;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.menu.MenuBuilder;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.api.events.RaceReclaimItemsEvent;
import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.hook.VaultAPIHook;
import net.pwing.races.util.menu.ConfirmationMenu;
import net.pwing.races.util.menu.IConfirmationHandler;
import net.pwing.races.race.skilltree.PwingRaceSkilltreeMenu;
import net.pwing.races.util.item.ItemUtil;
import net.pwing.races.util.MessageUtil;
import net.pwing.races.util.RaceMaterial;
import net.pwing.races.util.RaceSound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PwingRaceMenu implements RaceMenu {

    private PwingRaces plugin;

    private String name;
    private int slots;
    private boolean glassFilled;

    private Map<String, RaceIconData> cachedIcons;

    public PwingRaceMenu(PwingRaces plugin, String name, int slots, boolean glassFilled) {
        this.plugin = plugin;

        this.name = name;
        this.slots = slots;
        this.glassFilled = glassFilled;

        this.cachedIcons = new HashMap<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Race race : plugin.getRaceManager().getRaces()) {
                cachedIcons.put(race.getName(), race.getIconData());
            }
        });
    }

    public void openMenu(Player player) {
        MenuBuilder builder = new MenuBuilder(plugin, name, slots);

        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (glassFilled) {
            for (int i = 0; i < builder.getInventorySize(); i++) {
                if (builder.toInventory().getItem(i) == null || builder.toInventory().getItem(i).getType() == Material.AIR)
                    builder.setItem(new ItemBuilder(RaceMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("&a"), i);

            }
        }

        for (Race race : raceManager.getRaces()) {
            RaceData data = racePlayer.getRaceData(race);
            if (data == null)
                continue;

            RaceIconData iconData = cachedIcons.get(race.getName());
            ItemStack raceItem = race.getIconData().getUnlockedIcon();

            if (!data.isUnlocked())
                raceItem = iconData.getLockedIcon().orElse(iconData.getUnlockedIcon());
            else if (racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race))
                raceItem = iconData.getSelectedIcon().orElse(iconData.getUnlockedIcon());

            if (iconData.getIconSlot() >= 0) {
                builder.setItem(raceItem, iconData.getIconSlot()).addClickEvent(iconData.getIconSlot(), (player1, action, item) -> {
                    if (data.isUnlocked())
                        openRaceMenu(player1, race);
                    else
                        MessageUtil.sendMessage(player1, "locked-race", "%prefix% &cYou have not unlocked this race yet!");
                });
            }
        }

        builder.open(player);
    }

    public void openRaceMenu(Player player, Race race) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        RaceData data = racePlayer.getRaceData(race);
        MenuBuilder builder = new MenuBuilder(plugin, MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("race-gui", "%race% Race").replace("%race%", race.getDisplayName())));

        ItemBuilder info = new ItemBuilder(cachedIcons.get(race.getName()).getUnlockedIcon().clone());

        List<String> lore = new ArrayList<>();
        String level = MessageUtil.getMessage("menu-level", "&7Level: &3") + data.getLevel();
        String experience = MessageUtil.getMessage("menu-experience", "&7Experience: &3") + MessageUtil.getMessage("menu-max-level", "Max Level");
        if (race.getRaceLevelMap().containsKey(data.getLevel()))
            experience = MessageUtil.getMessage("menu-experience", "&7Experience: &3") + data.getExperience() + ChatColor.GRAY + " / " + ChatColor.DARK_AQUA + race.getRequiredExperience(data.getLevel());

        String skillpoint = MessageUtil.getMessage("menu-remaining-skillpoints", "&7Remaining Skillpoints: &3") + data.getUnusedSkillpoints();
        lore.add(level);
        lore.add(experience);
        lore.add(skillpoint);
        info.setLore(lore);

        int pointCost = plugin.getConfigManager().getReclaimSkillpointCost();

        ItemBuilder reclaimItems = new ItemBuilder(Material.CHEST).setName(MessageUtil.getMessage("menu-reclaim-race-items", "&b&lReclaim Race Items")).setLore(
                MessageUtil.getMessage("menu-reclaim-race-items-lore", "&7Reclaim your race items if you lost them."));
        ItemBuilder reclaimSkillpoints = new ItemBuilder(Material.PRISMARINE_SHARD).setName(MessageUtil.getMessage("menu-reclaim-skillpoints", "&b&lReclaim Skillpoints")).setLore(
                MessageUtil.getMessage("menu-reclaim-skillpoints-lore", "&7Reclaim all your spent skillpoints. \n&cResets all your purchased skills.") +
                "\n" + MessageUtil.getMessage("menu-cost-display", "&7Cost: &a") + pointCost + " " + plugin.getVaultHook().getCurrencyName(pointCost));

        boolean allowReclaim = plugin.getConfigManager().isAllowReclaimingSkillpoints();
        int cost = plugin.getConfigManager().getReclaimItemsCost();

        if (allowReclaim && cost > 0) {
            List<String> reclaimLore = reclaimItems.toItemStack().getItemMeta().getLore();
            reclaimLore.add(MessageUtil.getMessage("menu-cost-display", "&7Cost: &a") + cost + " " + plugin.getVaultHook().getCurrencyName(cost));
            reclaimItems.setLore(reclaimLore);
        }

        for (int slot : race.getSkilltreeMap().keySet()) {
            Optional<RaceSkilltree> skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(race.getSkilltreeMap().get(slot));
            if (!skilltree.isPresent())
                continue;

            builder.setItem(skilltree.get().getIcon(), slot).addClickEvent(slot, (player12, action, item) -> new PwingRaceSkilltreeMenu(plugin, race, skilltree.get()).openMenu(player12));
        }

        RaceConfigurationManager configManager = plugin.getConfigManager();
        builder.setItem(info, 13).addClickEvent(13, (clickedPlayer, action, item) -> {
            if (action == ClickType.RIGHT) {
                openMenu(clickedPlayer);
            } else if (action == ClickType.LEFT) {
                if (racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race))
                    return;

                ConfirmationMenu menu = new ConfirmationMenu(plugin, MessageUtil.getMessage("menu-confirmation", "Confirmation"),
                        MessageUtil.getMessage("menu-confirm", "&aConfirm"), MessageUtil.getMessage("menu-cancel", "&cCancel Purchase"), new IConfirmationHandler() {

                    @Override
                    public void onConfirm(Player player, ClickType action, ItemStack item) {
                        if (plugin.getConfigManager().isRaceUnlockUsesCost()) {
                            if (plugin.getConfigManager().getRaceChangeCostType().equalsIgnoreCase("money")) {
                                if (!plugin.getVaultHook().hasBalance(player, plugin.getConfigManager().getRaceChangeCost())) {
                                    MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                    player.closeInventory();
                                    return;
                                } else {
                                    plugin.getVaultHook().withdrawPlayer(player, plugin.getConfigManager().getRaceChangeCost());
                                }
                            }

                            if (plugin.getConfigManager().getRaceChangeCostType().equalsIgnoreCase("exp")) {
                                if (player.getTotalExperience() < plugin.getConfigManager().getRaceChangeCost()) {
                                    MessageUtil.sendMessage(player, "not-enough-exp", "%prefix% &cYou do not have enough experience for this transaction!");
                                    player.closeInventory();
                                    return;
                                } else {
                                    float cost = player.getTotalExperience() - plugin.getConfigManager().getRaceChangeCost();
                                    player.giveExpLevels(-100000);
                                    player.setExp(0.0f);
                                    player.setTotalExperience(0);
                                    player.giveExp((int) cost);
                                }
                            }
                        }

                        RaceChangeEvent event = new RaceChangeEvent(player, racePlayer.getRace().get(), race);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cannot-set-race", "%prefix% &cCannot set race.")));
                            player.closeInventory();
                            return;
                        }

                        racePlayer.setActiveRace(event.getNewRace());

                        // This needs to be called again for the new race as well
                        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change");
                        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change " + event.getNewRace().getName());

                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "set-your-active-race", "%prefix% Successfully set your race to %race%!");
                    }

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-race-change", "%prefix% &cCancelled race change.");
                    }
                });

                if (plugin.getConfigManager().isAllowPlayerRaceChanges() && (racePlayer.getRace().isPresent() || !racePlayer.getRace().get().equals(race)))
                    menu.open(clickedPlayer);
            }
        });

        if (configManager.isAllowReclaimingSkillpoints() && racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race)) {
            builder.setItem(reclaimSkillpoints, 11).addClickEvent(11, (clickedPlayer, action, item) -> {
                ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-skillpoint-claim", "%prefix% &cCancelled skillpoint reclaim.");
                    }

                    // TODO: Add a "reduction" system farther down the line
                    @Override
                    public void onConfirm(Player player, ClickType action, ItemStack item) {
                        int cost1 = configManager.getReclaimSkillpointCost();

                        // double reduction = configManager.getReclaimSkillpointReduction();
                        VaultAPIHook vaultHook = plugin.getVaultHook();
                        if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost1)) {
                            MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                            player.closeInventory();
                            return;
                        }

                        if (data.getPurchasedElementsMap().isEmpty()) {
                            MessageUtil.sendMessage(player, "no-elements-purchased", "%prefix% &cYou haven't bought any skilltree elements!");
                            player.closeInventory();
                            return;
                        }

                        // double pointReduction = data.getUsedSkillpoints() * reduction;
                        int finalReduction = data.getUsedSkillpoints(); // - (int) pointReduction;

                        RaceReclaimSkillpointsEvent event = new RaceReclaimSkillpointsEvent(player, race, data.getUnusedSkillpoints(), data.getUnusedSkillpoints() + finalReduction);
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled())
                            return;

                        vaultHook.withdrawPlayer(player, cost1);

                        data.setUnusedSkillpoints(event.getNewSkillpointCount());
                        data.setUsedSkillpoints(0);

                        for (String tree : data.getPurchasedElementsMap().keySet())
                            data.getPurchasedElementsMap().put(tree, new ArrayList<>());

                        MessageUtil.sendMessage(player, "race-skillpoint-claim", "%prefix% Successfully reclaimed your used skillpoints!");
                        player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
                        openRaceMenu(player, race);
                    }
                });

                menu.open(clickedPlayer);
            });
        }

        if (configManager.isAllowReclaimingItems() && racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race)) {
            builder.setItem(reclaimItems, 15).addClickEvent(15, (clickedPlayer, action, item) -> {
                ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-item-claim", "%prefix% &cCancelled item reclaim.");
                    }

                    @Override
                    public void onConfirm(Player player, ClickType action, ItemStack item) {
                        int cost12 = configManager.getReclaimItemsCost();

                        VaultAPIHook vaultHook = plugin.getVaultHook();
                        if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost12)) {
                            MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                            player.closeInventory();
                            return;
                        }

                        RaceReclaimItemsEvent event = new RaceReclaimItemsEvent(player, race, race.getRaceItems().values());
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled())
                            return;

                        vaultHook.withdrawPlayer(player, cost12);
                        race.getRaceItems().values().forEach(raceItem -> ItemUtil.addItem(player, raceItem));
                        MessageUtil.sendMessage(player, "race-item-claim", "%prefix% Sucessfully reclaimed your race items!");
                        player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
                        openRaceMenu(player, race);
                    }
                });

                menu.open(clickedPlayer);
            });
        }

        builder.open(player);
    }
}
