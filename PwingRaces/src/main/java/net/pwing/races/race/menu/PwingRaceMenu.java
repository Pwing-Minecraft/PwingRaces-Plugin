package net.pwing.races.race.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.menu.RaceIconData;
import net.pwing.races.api.race.menu.RaceMenu;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.builder.ItemBuilder;
import net.pwing.races.builder.MenuBuilder;
import net.pwing.races.config.RaceConfigurationManager;
import net.pwing.races.api.events.RaceReclaimItemsEvent;
import net.pwing.races.api.events.RaceReclaimSkillpointsEvent;
import net.pwing.races.hooks.VaultAPIHook;
import net.pwing.races.menu.ConfirmationMenu;
import net.pwing.races.menu.IConfirmationHandler;
import net.pwing.races.menu.IMenuClickHandler;
import net.pwing.races.race.skilltree.PwingRaceSkilltreeMenu;
import net.pwing.races.utilities.MessageUtil;
import net.pwing.races.utilities.RaceMaterial;
import net.pwing.races.utilities.RaceSound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

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

        this.cachedIcons = new HashMap<String, RaceIconData>();
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
                raceItem = iconData.getLockedIcon();
            else if (racePlayer.getActiveRace() != null && racePlayer.getActiveRace().equals(race))
                raceItem = iconData.getSelectedIcon();

            if (iconData.getIconSlot() >= 0) {
                builder.setItem(raceItem, iconData.getIconSlot()).addClickEvent(iconData.getIconSlot(), new IMenuClickHandler() {

                    @Override
                    public void onClick(Player player, ClickType action, ItemStack item) {
                        if (data.isUnlocked())
                            openRaceMenu(player, race);
                        else
                            MessageUtil.sendMessage(player, "locked-race", "%prefix% &cYou have not unlocked this race yet!");
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
        MenuBuilder builder = new MenuBuilder(plugin, MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("race-gui", "%race% Race").replace("%race%", race.getName())));

        ItemBuilder info = new ItemBuilder(cachedIcons.get(race.getName()).getUnlockedIcon().clone());

        List<String> lore = new ArrayList<String>();
        String level = ChatColor.GRAY + "Level: " + ChatColor.DARK_AQUA + data.getLevel();
        String experience = ChatColor.GRAY + "Experience: " + ChatColor.DARK_AQUA + "Max Level";
        if (race.getRaceLevelMap().containsKey(data.getLevel()))
            experience = ChatColor.GRAY + "Experience: " + ChatColor.DARK_AQUA + "" + data.getExperience() + ChatColor.GRAY + " / " + ChatColor.DARK_AQUA + race.getRequiredExperience(data.getLevel());

        String skillpoint = ChatColor.GRAY + "Remaining Skill Points: " + ChatColor.DARK_AQUA + data.getUnusedSkillpoints();
        lore.add(level);
        lore.add(experience);
        lore.add(skillpoint);
        info.setLore(lore);

        int pointCost = plugin.getConfigManager().getReclaimSkillpointCost();

        ItemBuilder reclaimItems = new ItemBuilder(Material.CHEST).setName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Reclaim Race Items").setLore(ChatColor.GRAY + "Reclaim your race items if you lost them.");
        ItemBuilder reclaimSkillpoints = new ItemBuilder(Material.PRISMARINE_SHARD).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "Reclaim Skillpoints").setLore(ChatColor.GRAY + "Reclaim all your spent skillpoints.", ChatColor.RED + "Resets all your purchased skills.",
            ChatColor.GRAY + "Cost: " + ChatColor.GREEN + pointCost + " " + plugin.getVaultHook().getCurrencyName(pointCost));

        boolean allowReclaim = plugin.getConfigManager().isReclaimingSkillpointsAllowed();
        int cost = plugin.getConfigManager().getReclaimItemsCost();

        if (allowReclaim && cost > 0) {
            List<String> reclaimLore = reclaimItems.toItemStack().getItemMeta().getLore();
            reclaimLore.add(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + cost + " " + plugin.getVaultHook().getCurrencyName(cost));
            reclaimItems.setLore(reclaimLore);
        }

        for (int slot : race.getSkilltreeMap().keySet()) {
            RaceSkilltree skilltree = raceManager.getSkilltreeManager().getSkilltreeFromName(race.getSkilltreeMap().get(slot));
            if (skilltree == null)
                continue;

            builder.setItem(skilltree.getIcon(), slot).addClickEvent(slot, new IMenuClickHandler() {

                @Override
                public void onClick(Player player, ClickType action, ItemStack item) {
                    new PwingRaceSkilltreeMenu(plugin, race, skilltree).openMenu(player);
                }
            });
        }

        RaceConfigurationManager configManager = plugin.getConfigManager();
        builder.setItem(info, 13).addClickEvent(13, new IMenuClickHandler() {

            @Override
            public void onClick(Player player, ClickType action, ItemStack item) {
                if (action == ClickType.RIGHT) {
                    openMenu(player);
                } else if (action == ClickType.LEFT) {
                    ConfirmationMenu menu = new ConfirmationMenu(plugin, "Confirmation", "Confirm", "Cancel", new IConfirmationHandler() {

                        @Override
                        public void onConfirm(Player player, ClickType action, ItemStack item) {
                            racePlayer.setActiveRace(race);
                            openRaceMenu(player, race);
                            MessageUtil.sendMessage(player, "set-your-active-race", "%prefix% Successfully set your race to %race%!");
                        }

                        @Override
                        public void onDeny(Player player, ClickType action, ItemStack item) {
                            openRaceMenu(player, race);
                            MessageUtil.sendMessage(player, "cancelled-race-change", "%prefix% &cCancelled race change.");
                        }
                    });

                    if (plugin.getConfigManager().isPlayerRaceChangesAllowed() && (racePlayer.getActiveRace() == null || !racePlayer.getActiveRace().equals(race)))
                        menu.open(player);
                }
            }
        });

        if (configManager.isReclaimingSkillpointsAllowed() && racePlayer.getActiveRace() != null && racePlayer.getActiveRace().equals(race)) {
            builder.setItem(reclaimSkillpoints, 11).addClickEvent(11, new IMenuClickHandler() {

                @Override
                public void onClick(Player player, ClickType action, ItemStack item) {
                    ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                        @Override
                        public void onDeny(Player player, ClickType action, ItemStack item) {
                            openRaceMenu(player, race);
                            MessageUtil.sendMessage(player, "cancelled-skillpoint-claim", "%prefix% &cCancelled skillpoint reclaim.");
                        }

                        // Add a "reduction" system farther down the line
                        @Override
                        public void onConfirm(Player player, ClickType action, ItemStack item) {
                            int cost = configManager.getReclaimSkillpointCost();

                            // double reduction = configManager.getReclaimSkillpointReduction();
                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost)) {
                                MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                return;
                            }

                            // double pointReduction = data.getUsedSkillpoints() * reduction;
                            int finalReduction = data.getUsedSkillpoints(); // - (int) pointReduction;

                            RaceReclaimSkillpointsEvent event = new RaceReclaimSkillpointsEvent(player, race, data.getUnusedSkillpoints(), data.getUnusedSkillpoints() + finalReduction);
                            Bukkit.getPluginManager().callEvent(event);

                            if (event.isCancelled())
                                return;

                            vaultHook.withdrawPlayer(player, cost);

                            data.setUnusedSkillpoints(event.getNewSkillpointCount());
                            data.setUsedSkillpoints(0);

                            for (String tree : data.getPurchasedElementsMap().keySet())
                                data.getPurchasedElementsMap().put(tree, new ArrayList<String>());

                            MessageUtil.sendMessage(player, "race-skillpoint-claim", "%prefix% Successfully reclaimed your used skillpoints!");
                            player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
                            openRaceMenu(player, race);
                        }
                    });

                    menu.open(player);
                }
            });
        }

        if (configManager.isReclaimingItemsAllowed() && racePlayer.getActiveRace() != null && racePlayer.getActiveRace().equals(race)) {
            builder.setItem(reclaimItems, 15).addClickEvent(15, new IMenuClickHandler() {

                @Override
                public void onClick(Player player, ClickType action, ItemStack item) {
                    ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                        @Override
                        public void onDeny(Player player, ClickType action, ItemStack item) {
                            openRaceMenu(player, race);
                            MessageUtil.sendMessage(player, "cancelled-item-claim", "%prefix% &cCancelled item reclaim.");
                        }

                        @Override
                        public void onConfirm(Player player, ClickType action, ItemStack item) {
                            int cost = configManager.getReclaimItemsCost();

                            VaultAPIHook vaultHook = plugin.getVaultHook();
                            if (vaultHook.hasEconomy() && !vaultHook.hasBalance(player, cost)) {
                                MessageUtil.sendMessage(player, "not-enough-money", "%prefix% &cYou do not have enough %currency-name-plural% for this transaction!");
                                return;
                            }

                            RaceReclaimItemsEvent event = new RaceReclaimItemsEvent(player, race, race.getRaceItems().values());
                            Bukkit.getPluginManager().callEvent(event);

                            if (event.isCancelled())
                                return;

                            vaultHook.withdrawPlayer(player, cost);
                            race.getRaceItems().values().forEach(raceItem -> player.getInventory().addItem(raceItem));
                            MessageUtil.sendMessage(player, "race-item-claim", "%prefix% Sucessfully reclaimed your race items!");
                            player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
                            openRaceMenu(player, race);
                        }
                    });

                    menu.open(player);
                }
            });
        }

        builder.open(player);
    }
}
