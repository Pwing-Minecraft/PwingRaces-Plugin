package net.pwing.races.race.editor.menu;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceItemDefinition;
import net.pwing.races.race.editor.RaceEditorManager;
import net.pwing.races.race.menu.MenuHandlers;
import net.pwing.races.util.InteractionInputs;
import net.pwing.races.util.InventoryUtil;
import net.pwing.races.util.item.ItemBuilder;
import net.pwing.races.util.menu.ConfirmationMenu;
import net.pwing.races.util.menu.IConfirmationHandler;
import net.pwing.races.util.menu.MenuBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RaceItemsMenu {
    private final Race race;

    public RaceItemsMenu(Race race) {
        this.race = race;
    }

    public void openMenu(Player player) {
        Map<String, RaceItemDefinition> items = this.race.getItemDefinitions();

        int slots = 18 + InventoryUtil.getRows(items.size()) * 9;
        MenuBuilder builder = MenuBuilder.builder(PwingRaces.getInstance(), "Race Editor > Race Items", slots);

        int i = 0;
        for (Map.Entry<String, RaceItemDefinition> entry : new HashSet<>(items.entrySet())) {
            RaceItemDefinition definition = entry.getValue();
            int slot = InventoryUtil.getPagedSlot(i++);
            builder.item(getMenuItem(definition), slot, (clicker, action, clickedItem) -> {
                if (action == ClickType.LEFT) {
                    definition.giveToPlayer(!definition.giveToPlayer());
                    race.save();

                    // Update item (will not reset click handler)
                    builder.item(getMenuItem(definition), slot);
                } else if (action == ClickType.RIGHT) {
                    new ConfirmationMenu(PwingRaces.getInstance(), "Confirm Item Deletion", "Confirm", "Deny", new IConfirmationHandler() {

                        @Override
                        public boolean onConfirm(Player player, ClickType action, ItemStack item) {
                            items.remove(entry.getKey());
                            race.save();

                            openMenu(player);
                            return true;
                        }

                        @Override
                        public void onDeny(Player player, ClickType action, ItemStack item) {
                            openMenu(player);
                        }
                    }).open(player);
                }
            });
        }

        builder.item(ItemBuilder.builder(Material.EMERALD)
                        .name("&aAdd Item")
                        .lore(List.of("&7Click to add a new item.")),
                4,
                MenuHandlers.simple(() -> {
                    player.closeInventory();
                    player.sendMessage(ChatColor.AQUA + "Select the item in your inventory to add as a race item.");

                    new InteractionInputs.InventoryInput(player) {

                        @Override
                        public void onInventoryInteract(ItemStack item) {
                            items.put(UUID.randomUUID().toString(), new RaceItemDefinition(item, true));
                            race.save();

                            openMenu(player);
                        }
                    };
                }));

        builder.open(player);
    }

    private static ItemStack getMenuItem(RaceItemDefinition definition) {
        ItemStack item = definition.itemStack();

        List<String> lore = item.hasItemMeta() && item.getItemMeta().hasLore() ? new ArrayList<>(item.getItemMeta().getLore()) : new ArrayList<>();
        lore.add("");
        lore.add("&fGiven to Player: " + (definition.giveToPlayer() ? "&aYes" : "&cNo"));
        lore.add("");
        lore.add("&eLeft-Click: &fToggle given to player");
        lore.add("&eRight-Click: &cRemove Item");

        return ItemBuilder.builder(item)
                .lore(lore)
                .build();
    }
}
