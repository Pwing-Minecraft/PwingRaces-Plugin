package net.pwing.races.race.editor.menu.edit;

import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.menu.RaceEditMenu;
import net.pwing.races.race.editor.menu.button.EditorButton;
import net.pwing.races.race.editor.wizard.BaseEditorContext;
import net.pwing.races.race.editor.wizard.RaceEditorWizards;
import net.pwing.races.race.editor.wizard.stage.ItemStackInputStage;
import net.pwing.races.util.item.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectedIconButton extends EditorButton {

    public SelectedIconButton(RaceEditMenu menu, Race race) {
        super(menu, race);
    }

    @Override
    public ItemStack getItem() {
        ItemStack icon = this.race.getIconData().getSelectedIcon().orElse(this.race.getIconData().getUnlockedIcon());
        List<String> lore = new ArrayList<>(Arrays.asList(
                "&7Click to change the race icon shown for players",
                "&7that have it selected.",
                "",
                "&fCurrent:",
                "&7- &fItem: &b" + WordUtils.capitalize(icon.getType().getKey().getKey().replace("_", " "))
        ));

        if (icon.hasItemMeta()) {
            ItemMeta meta = icon.getItemMeta();
            if (meta.hasDisplayName()) {
                lore.add("&7- &fDisplay Name: &b" + meta.getDisplayName());
            }

            if (meta.hasLore()) {
                lore.add("&7- &fLore:");
                for (String line : meta.getLore()) {
                    lore.add("&7  - &f" + line);
                }
            }

            if (meta.hasEnchants()) {
                lore.add("&7- &fEnchants:");
                for (Enchantment enchant : meta.getEnchants().keySet()) {
                    lore.add("&7  - &f" + WordUtils.capitalize(enchant.getKey().getKey().replace("_", " ")) + " " + meta.getEnchants().get(enchant));
                }
            }
        }


        return ItemBuilder.builder(Material.GOLD_BLOCK)
                .name("&aRace Icon &e(Selected)")
                .lore(lore)
                .build();
    }

    @Override
    public void onClick(Player player, ClickType action) {
        RaceEditorWizards.createWizard(BaseEditorContext::new)
                .addStage(new ItemStackInputStage<>(ChatColor.AQUA + "Select the item in your inventory to use as the selected race icon.", context -> this.race.getIconData()::setSelectedIcon))
                .onComplete(context -> {
                    this.race.save();

                    context.getPlayer().sendMessage(ChatColor.GREEN + "Changed the selected race icon successfully!");
                    this.menu.openMenu(context.getPlayer());
                })
                .onCancel(context -> context.getPlayer().sendMessage(ChatColor.RED + "Cancelled icon selection."))
                .openWizard(player);
    }
}
