package net.pwing.races.race.editor.menu.edit;

import net.pwing.races.api.race.Race;
import net.pwing.races.race.editor.menu.RaceEditMenu;
import net.pwing.races.race.editor.menu.button.EditorButton;
import net.pwing.races.race.editor.wizard.BaseEditorContext;
import net.pwing.races.race.editor.wizard.RaceEditorWizards;
import net.pwing.races.race.editor.wizard.stage.TextInputStage;
import net.pwing.races.util.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NameButton extends EditorButton {

    public NameButton(RaceEditMenu menu, Race race) {
        super(menu, race);
    }

    @Override
    public ItemStack getItem() {
        return ItemBuilder.builder(Material.PAPER)
                .name("&aRace Name")
                .lore(List.of(
                        "&7Click to change the race name.",
                        "",
                        "&fCurrent: " + this.race.getName(),
                        "&cWarning: &fAny player data associated with this race",
                        "&fwill be lost by renaming it. &cProceed with caution!"
                ))
                .build();
    }

    @Override
    public void onClick(ClickType action) {
        RaceEditorWizards.createWizard(BaseEditorContext::new)
                .addStage(new TextInputStage<>(ChatColor.AQUA + "Type the name of the race in chat or \"cancel\" to cancel.", context -> this.race::setName))
                .onComplete(context -> {
                    context.getPlayer().sendMessage(ChatColor.GREEN + "Renamed race successfully!");
                    this.menu.openMenu(context.getPlayer());
                });
    }
}
