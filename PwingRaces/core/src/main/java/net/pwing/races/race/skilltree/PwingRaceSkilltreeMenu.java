package net.pwing.races.race.skilltree;

import lombok.AllArgsConstructor;
import net.pwing.races.PwingRaces;
import net.pwing.races.api.events.RaceElementPurchaseEvent;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RaceManager;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.api.race.skilltree.RaceSkilltreeElement;
import net.pwing.races.util.MessageUtil;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.menu.ConfirmationMenu;
import net.pwing.races.util.menu.IConfirmationHandler;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PwingRaceSkilltreeMenu {

    private PwingRaces plugin;
    private Race race;
    private RaceSkilltree skilltree;

    public void openMenu(Player player) {
        RaceManager raceManager = plugin.getRaceManager();
        RacePlayer racePlayer = raceManager.getRacePlayer(player);
        MenuBuilder builder = MenuBuilder.builder(plugin, skilltree.getName() + " Skilltree", skilltree.getMenuSize());

        List<RaceSkilltreeElement> elements = skilltree.getElements();
        if (skilltree.getMenuSlot() >= 0)
            builder.item(skilltree.getMenuIcon(), skilltree.getMenuSlot());

        builder.clickEvent(skilltree.getMenuSlot(), (player1, action, item) -> raceManager.getRaceMenu().openRaceMenu(player1, race));

        for (RaceSkilltreeElement element : elements) {
            ItemStack elementItem = getElementItem(player, element);
            builder.item(elementItem, element.getSlot());

            boolean canPurchase = true;
            RaceData raceData = racePlayer.getRaceData(race);
            if (raceData.hasPurchasedElement(skilltree.getInternalName(), element.getInternalName())) {
                canPurchase = false;
            }

            int parentsPurchased = 0;
            for (String parent : element.getParentElements()) {
                if (raceData.hasPurchasedElement(skilltree.getInternalName(), parent))
                    parentsPurchased += 1;
            }

            if (parentsPurchased < element.getRequiredParentAmount() && !element.getParentElements().contains("none")) {
                canPurchase = false;
            }

            if (!racePlayer.getRace().isPresent() || !racePlayer.getRace().get().equals(race)) {
                canPurchase = false;
            }

            boolean finalCanPurchase = canPurchase;
            builder.clickEvent(element.getSlot(), canPurchase && raceData.getUnusedSkillpoints() >= element.getCost(), (player2, action, item) -> {
                if (!finalCanPurchase) {
                    return;
                }

                if (raceData.getUnusedSkillpoints() < element.getCost()) {
                    MessageUtil.sendMessage(player2, "invalid-skillpoints", "%prefix% &cYou do not have the required skillpoints to purchase this upgrade!");
                    player2.closeInventory();
                    return;
                }

                ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

                    @Override
                    public void onDeny(Player player, ClickType action, ItemStack item) {
                        player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cancelled-purchase", "%prefix% &cCancelled purchase of %element%.").replace("%element%", element.getTitle())));
                        openMenu(player);
                    }

                    @Override
                    public boolean onConfirm(Player player, ClickType action, ItemStack item) {
                        // In the unlikely scenario skillpoints are updated before confirmation
                        if (!raceData.hasPurchasedElement(skilltree.getInternalName(), element.getInternalName())) {
                            if (raceData.getUnusedSkillpoints() < element.getCost()) {
                                MessageUtil.sendMessage(player, "invalid-skillpoints", "%prefix% &cYou do not have the required skillpoints to purchase this upgrade!");
                                player.closeInventory();
                                return false;
                            }

                            RaceElementPurchaseEvent event = new RaceElementPurchaseEvent(player, race, element);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return false;
                            }

                            raceData.setUsedSkillpoints(raceData.getUsedSkillpoints() + element.getCost());
                            raceData.setUnusedSkillpoints(raceData.getUnusedSkillpoints() - element.getCost());
                            raceData.addPurchasedElement(skilltree.getInternalName(), element.getInternalName());

                            player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("successful-purchase", "%prefix% You have successfully purchased the %element% upgrade for the %skilltree% skilltree!").replace("%element%", element.getTitle()).replace("%skilltree%", skilltree.getName())));
                            player.playSound(player.getLocation(), plugin.getConfigManager().getSuccessSound(), 1f, 1f);
                            openMenu(player);
                            return true;
                        } else {
                            MessageUtil.sendMessage(player, "already-purchased", "%prefix% &cYou have already purchased this upgrade.");
                            player.closeInventory();
                            return false;
                        }
                    }
                });

                menu.open(player);
            });
        }

        builder.open(player);
    }

    public ItemStack getElementItem(Player player, RaceSkilltreeElement element) {
        RaceManager raceManager = plugin.getRaceManager();
        RaceData data = raceManager.getPlayerData(player, race);

        List<String> lore = new ArrayList<>(element.getDescription());
        if (data.hasPurchasedElement(skilltree.getInternalName(), element.getInternalName())) {
            ItemStack purchasedIcon = ItemBuilder.builder(Material.LIME_STAINED_GLASS_PANE)
                    .name(ChatColor.WHITE + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.GREEN + "Purchased")
                    .lore(element.getDescription())
                    .build();

            if (element.getPurchasedIcon().isPresent())
                return mergeIconWithElementItem(element.getPurchasedIcon().get(), purchasedIcon);

            return purchasedIcon;
        }

        int parentsPurchased = 0;
        List<String> parents = new ArrayList<>(element.getParentElements());
        for (String parent : element.getParentElements()) {
            if (parent.contains(":")) {
                String[] split = parent.split(":");
                if (data.hasPurchasedElement(split[0], split[1])) {
                    parents.remove(parent);
                    parentsPurchased += 1;
                }
            } else {
                if (data.hasPurchasedElement(skilltree.getInternalName(), parent)) {
                    parents.remove(parent);
                    parentsPurchased += 1;
                }
            }
        }

        if (parentsPurchased >= element.getRequiredParentAmount() || parents.size() >= 1 && parents.contains("none")) {
            lore.add(MessageUtil.getMessage("menu-skilltree-skillpoint-cost", "&7Skillpoint Cost: &a") + element.getCost());

            if (raceManager.getRacePlayer(player).getRace().map(selectedRace -> selectedRace.equals(race)).orElse(false)) {
                lore.add(MessageUtil.getMessage("menu-skilltree-purchase", "&eClick to purchase."));
            }

            ItemStack unlockedIcon = ItemBuilder.builder(Material.ORANGE_STAINED_GLASS_PANE)
                    .name(ChatColor.WHITE + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Unlocked")
                    .lore(lore)
                    .build();

            if (element.getIcon() != null)
                return mergeIconWithElementItem(element.getIcon(), unlockedIcon);

            return unlockedIcon;
        }

        // If for some reason the required amount of elements is higher than the skilltree parent amount
        String purchaseString = "the previous skill";
        if (parents.size() >= 1) {
            Optional<RaceSkilltreeElement> skilltreeElement = skilltree.getElementFromName(parents.get(0));
            if (skilltreeElement.isPresent())
                purchaseString = skilltreeElement.get().getTitle();

        }

        String unlock = MessageUtil.getMessage("menu-skilltree-unlock", "&cYou must unlock %element% before \n&cpurchasing this upgrade.")
                .replace("%element%", purchaseString);

        lore.add(unlock);

        StringBuilder loreString = new StringBuilder();
        for (String str : lore) {
            loreString.append(str).append("\n");
        }

        ItemStack lockedItem = ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE)
                .name(ChatColor.RED + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.DARK_RED + "Locked")
                .lore(loreString.toString())
                .build();

        if (element.getLockedIcon().isPresent())
            return mergeIconWithElementItem(element.getLockedIcon().get(), lockedItem);

        return lockedItem;
    }

    private ItemStack mergeIconWithElementItem(ItemStack icon, ItemStack elementIcon) {
        ItemMeta meta = icon.getItemMeta();
        if (!meta.hasDisplayName()) {
            // italic for some reason ingame, so add chatcolor before
            meta.setDisplayName(ChatColor.WHITE + elementIcon.getItemMeta().getDisplayName());
        }

        if (!meta.hasLore() || meta.getLore().isEmpty()) {
            meta.setLore(elementIcon.getItemMeta().getLore());
        }

        icon.setItemMeta(meta);
        return icon;
    }
}
