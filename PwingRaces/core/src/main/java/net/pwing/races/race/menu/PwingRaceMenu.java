package net.pwing.races.race.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceChangeEvent;
import net.pwing.races.api.events.RaceReclaimItemsEvent;
import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.menu.RaceIconData;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.hook.VaultAPIHook;
import net.pwing.races.race.skilltree.PwingRaceSkilltreeMenu;
import net.pwing.races.util.MessageUtil;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.item.ItemUtil;
import net.pwing.races.util.menu.ConfirmationMenu;
import net.pwing.races.util.menu.IConfirmationHandler;
import net.pwing.races.util.menu.MenuBuilder;
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
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Race race : plugin.getRaceManager().getRaces()) {
                cachedIcons.put(race.getName(), race.getIconData());
            }
        });
    }

    public void openMenu(Player player) {
        MenuBuilder builder = MenuBuilder.builder(plugin, name, slots);

        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        if (racePlayer == null)
            return;

        if (glassFilled) {
            for (int i = 0; i < slots; i++) {
                if (builder.build().getItem(i) == null || builder.build().getItem(i).getType() == Material.AIR)
                    builder.item(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).name("&a"), i);

            }
        }

        for (Race race : raceManager.getRaces()) {
            RaceData data = racePlayer.getRaceData(race);
            if (data == null)
                continue;

            RaceIconData iconData = cachedIcons.computeIfAbsent(race.getName(), e -> race.getIconData());
            ItemStack raceItem = race.getIconData().getUnlockedIcon();

            if (!data.isUnlocked())
                raceItem = iconData.getLockedIcon().orElse(iconData.getUnlockedIcon());
            else if (racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race))
                raceItem = iconData.getSelectedIcon().orElse(iconData.getUnlockedIcon());

            if (iconData.getIconSlot() >= 0) {
                builder.item(raceItem, iconData.getIconSlot()).clickEvent(iconData.getIconSlot(), data.isUnlocked(), (player1, action, item) -> {
                    if (data.isUnlocked()) {
                        openRaceMenu(player1, race);
                    } else {
                        MessageUtil.sendMessage(player1, "locked-race", "%prefix% &cYou have not unlocked this race yet!");
                    }
                });
            }
        }

        builder.open(player);
    }

    public void openRaceMenu(Player player, Race race) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        RaceData data = racePlayer.getRaceData(race);
        MenuBuilder builder = MenuBuilder.builder(plugin, MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("race-gui", "%race% Race").replace("%race%", race.getDisplayName())));

        ItemBuilder info = ItemBuilder.builder(cachedIcons.get(race.getName()).getUnlockedIcon());

        List<String> lore = new ArrayList<>();
        String level = MessageUtil.getMessage("menu-level", "&7Level: &3") + data.getLevel();
        String experience = MessageUtil.getMessage("menu-experience", "&7Experience: &3") + MessageUtil.getMessage("menu-max-level", "Max Level");
        if (race.getRaceLevelMap().containsKey(data.getLevel()) && !race.isMaxLevel(data.getLevel()))
            experience = MessageUtil.getMessage("menu-experience", "&7Experience: &3") + data.getExperience() + ChatColor.GRAY + " / " + ChatColor.DARK_AQUA + race.getRequiredExperience(data.getLevel());

        String skillpoint = MessageUtil.getMessage("menu-remaining-skillpoints", "&7Remaining Skillpoints: &3") + data.getUnusedSkillpoints();
        lore.add(level);
        lore.add(experience);
        lore.add(skillpoint);
        info.lore(lore);

        int pointCost = plugin.getConfigManager().getReclaimSkillpointCost();

        ItemBuilder reclaimItems = ItemBuilder.builder(Material.CHEST).name(MessageUtil.getMessage("menu-reclaim-race-items", "&b&lReclaim Race Items")).lore(
                MessageUtil.getMessage("menu-reclaim-race-items-lore", "&7Reclaim your race items if you lost them."));
        ItemBuilder reclaimSkillpoints = ItemBuilder.builder(Material.PRISMARINE_SHARD).name(MessageUtil.getMessage("menu-reclaim-skillpoints", "&b&lReclaim Skillpoints")).lore(
                MessageUtil.getMessage("menu-reclaim-skillpoints-lore", "&7Reclaim all your spent skillpoints. \n&cResets all your purchased skills.") +
                "\n" + MessageUtil.getMessage("menu-cost-display", "&7Cost: &a") + pointCost + " " + plugin.getVaultHook().getCurrencyName(pointCost));

        boolean allowReclaim = plugin.getConfigManager().isAllowReclaimingSkillpoints();
        int cost = plugin.getConfigManager().getReclaimItemsCost();

        if (allowReclaim && cost > 0) {
            List<String> reclaimLore = reclaimItems.build().getItemMeta().getLore();
            reclaimLore.add(MessageUtil.getMessage("menu-cost-display", "&7Cost: &a") + cost + " " + plugin.getVaultHook().getCurrencyName(cost));
            reclaimItems.lore(reclaimLore);
        }

        for (Map.Entry<Integer, String> entry : race.getSkilltreeMap().entrySet()) {
            int slot = entry.getKey();
            Optional<RaceSkilltree> skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(entry.getValue());
            if (!skilltree.isPresent())
                continue;

            builder.item(skilltree.get().getIcon(), slot).clickEvent(slot, (player12, action, item) -> new PwingRaceSkilltreeMenu(plugin, race, skilltree.get()).openMenu(player12));
        }

        RaceConfigurationManager configManager = plugin.getConfigManager();
        builder.item(info, 13).clickEvent(13, (clickedPlayer, action, item) -> {
            if (action == ClickType.RIGHT) {
                openMenu(clickedPlayer);
            } else if (action == ClickType.LEFT) {
                if (racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race))
                    return;

                ConfirmationMenu menu = new ConfirmationMenu(plugin, MessageUtil.getMessage("menu-confirmation", "Confirmation"),
                        MessageUtil.getMessage("menu-confirm", "&aConfirm"), MessageUtil.getMessage("menu-cancel", "&cCancel Purchase"), new IConfirmationHandler() {

                    @Override
                    public boolean onConfirm(Player player, ClickType action, ItemStack item) {
                        if (plugin.getConfigManager().isRaceUnlockUsesCost()) {
                            if (plugin.getConfigManager().getRaceChangeCostType().equalsIgnoreCase("money")) {
                                if (!plugin.getVaultHook().hasBalance(player, plugin.getConfigManager().getRaceChangeCost())) {
                                    MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                    player.closeInventory();
                                    return false;
                                } else {
                                    plugin.getVaultHook().withdrawPlayer(player, plugin.getConfigManager().getRaceChangeCost());
                                }
                            }

                            if (plugin.getConfigManager().getRaceChangeCostType().equalsIgnoreCase("exp")) {
                                if (player.getTotalExperience() < plugin.getConfigManager().getRaceChangeCost()) {
                                    MessageUtil.sendMessage(player, "not-enough-exp", "%prefix% &cYou do not have enough experience for this transaction!");
                                    player.closeInventory();
                                    return false;
                                } else {
                                    float cost = player.getTotalExperience() - plugin.getConfigManager().getRaceChangeCost();
                                    player.giveExpLevels(-100000);
                                    player.setExp(0.0f);
                                    player.setTotalExperience(0);
                                    player.giveExp((int) cost);
                                }
                            }
                        }

                        RaceChangeEvent event = new RaceChangeEvent(player, racePlayer.getRace().orElse(null) /* is nullable */, race);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cannot-set-race", "%prefix% &cCannot set race.")));
                            player.closeInventory();
                            return false;
                        }

                        racePlayer.setRace(event.getNewRace());
                        racePlayer.getRaceData(event.getNewRace()).setHasPlayed(true);

                        // This needs to be called again for the new race as well
                        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change");
                        plugin.getRaceManager().getTriggerManager().runTriggers(player, "race-change " + event.getNewRace().getName());

                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "set-your-active-race", "%prefix% Successfully set your race to %race%!");
                        return true;
                    }

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-race-change", "%prefix% &cCancelled race change.");
                    }
                });
                if (plugin.getConfigManager().isAllowPlayerRaceChanges() && (racePlayer.getRace().isPresent() || !racePlayer.getRace().map(Race::getName).orElse("").equals(race.getName()))) {
                    menu.open(clickedPlayer);
                }
            }
        });

        if (configManager.isAllowReclaimingSkillpoints() && racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race)) {
            builder.item(reclaimSkillpoints, 11).clickEvent(11, (clickedPlayer, action, item) -> {
                ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-skillpoint-claim", "%prefix% &cCancelled skillpoint reclaim.");
                    }

                    // TODO: Add a "reduction" system farther down the line
                    @Override
                    public boolean onConfirm(Player player, ClickType action, ItemStack item) {
                        int cost1 = configManager.getReclaimSkillpointCost();

                        // double reduction = configManager.getReclaimSkillpointReduction();
                        String costType = configManager.getReclaimSkillpointCostType();
                        if (costType.equalsIgnoreCase("money")) {
                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost1)) {
                                MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                player.closeInventory();
                                return false;
                            }
                        } else if (costType.equalsIgnoreCase("exp")) {
                            if (player.getTotalExperience() < cost1) {
                                MessageUtil.sendMessage(player, "not-enough-exp", "%prefix% &cYou do not have enough experience for this transaction!");
                                player.closeInventory();
                                return false;
                            }
                        }

                        if (data.getPurchasedElementsMap().isEmpty()) {
                            MessageUtil.sendMessage(player, "no-elements-purchased", "%prefix% &cYou haven't bought any skilltree elements!");
                            player.closeInventory();
                            return false;
                        }

                        // double pointReduction = data.getUsedSkillpoints() * reduction;
                        int finalReduction = data.getUsedSkillpoints(); // - (int) pointReduction;

                        RaceReclaimSkillpointsEvent event = new RaceReclaimSkillpointsEvent(player, race, data.getUnusedSkillpoints(), data.getUnusedSkillpoints() + finalReduction);
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            return false;
                        }

                        if (costType.equalsIgnoreCase("money")) {
                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            vaultHook.withdrawPlayer(player, cost1);
                        } else if (costType.equalsIgnoreCase("exp")) {
                            float cost = player.getTotalExperience() - cost1;
                            player.giveExpLevels(-100000);
                            player.setExp(0.0f);
                            player.setTotalExperience(0);
                            player.giveExp((int) cost);
                        }

                        data.setUnusedSkillpoints(event.getNewSkillpointCount());
                        data.setUsedSkillpoints(0);

                        for (String tree : data.getPurchasedElementsMap().keySet())
                            data.getPurchasedElementsMap().put(tree, new ArrayList<>());

                        MessageUtil.sendMessage(player, "race-skillpoint-claim", "%prefix% Successfully reclaimed your used skillpoints!");
                        player.playSound(player.getLocation(), plugin.getConfigManager().getSuccessSound(), 1f, 1f);
                        openRaceMenu(player, race);
                        return true;
                    }
                });

                menu.open(clickedPlayer);
            });
        }

        if (configManager.isAllowReclaimingItems() && racePlayer.getRace().isPresent() && racePlayer.getRace().get().equals(race)) {
            builder.item(reclaimItems, 15).clickEvent(15, (clickedPlayer, action, item) -> {
                ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        openRaceMenu(player, race);
                        MessageUtil.sendMessage(player, "cancelled-item-claim", "%prefix% &cCancelled item reclaim.");
                    }

                    @Override
                    public boolean onConfirm(Player player, ClickType action, ItemStack item) {
                        int cost12 = configManager.getReclaimItemsCost();

                        String costType = configManager.getReclaimItemsCostType();
                        if (costType.equalsIgnoreCase("money")) {
                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost12)) {
                                MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                player.closeInventory();
                                return false;
                            }
                        } else if (costType.equalsIgnoreCase("exp")) {
                            if (player.getTotalExperience() < cost12) {
                                MessageUtil.sendMessage(player, "not-enough-exp", "%prefix% &cYou do not have enough experience for this transaction!");
                                player.closeInventory();
                                return false;
                            }
                        }

                        RaceReclaimItemsEvent event = new RaceReclaimItemsEvent(player, race, race.getRaceItems().values());
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return false;
                        }

                        if (costType.equalsIgnoreCase("money")) {
                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            vaultHook.withdrawPlayer(player, cost12);
                        } else if (costType.equalsIgnoreCase("exp")) {
                            float cost = player.getTotalExperience() - cost12;
                            player.giveExpLevels(-100000);
                            player.setExp(0.0f);
                            player.setTotalExperience(0);
                            player.giveExp((int) cost);
                        }

                        race.getRaceItems().values().forEach(raceItem -> ItemUtil.addItem(player, raceItem));
                        MessageUtil.sendMessage(player, "race-item-claim", "%prefix% Successfully reclaimed your race items!");
                        player.playSound(player.getLocation(), plugin.getConfigManager().getSuccessSound(), 1f, 1f);
                        openRaceMenu(player, race);
                        return true;
                    }
                });

                menu.open(clickedPlayer);
            });
        }
        builder.open(player);
    }
}
