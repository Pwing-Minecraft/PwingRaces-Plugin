package net.pwing.races.race.skilltree;

import java.util.ArrayList;
import java.util.List;

import net.pwing.races.PwingRaces;
import net.pwing.races.race.RacePlayer;
import net.pwing.races.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import net.pwing.races.builder.ItemBuilder;
import net.pwing.races.builder.MenuBuilder;
import net.pwing.races.events.RaceElementPurchaseEvent;
import net.pwing.races.menu.ConfirmationMenu;
import net.pwing.races.menu.IConfirmationHandler;
import net.pwing.races.menu.IMenuClickHandler;
import net.pwing.races.race.Race;
import net.pwing.races.race.RaceManager;
import net.pwing.races.race.RaceData;
import net.pwing.races.utilities.RaceMaterial;
import net.pwing.races.utilities.RaceSound;

public class RaceSkilltreeMenu {

	private PwingRaces plugin;
	private Race race;
	private RaceSkilltree skilltree;

	public RaceSkilltreeMenu(PwingRaces plugin, Race race, RaceSkilltree skilltree) {
		this.plugin = plugin;
		this.race = race;
		this.skilltree = skilltree;
	}

	public void openMenu(Player player) {
		RaceManager raceManager = plugin.getRaceManager();
		RacePlayer racePlayer = raceManager.getRacePlayer(player);
		MenuBuilder builder = new MenuBuilder(plugin, skilltree.getName() + " Skilltree", skilltree.getMenuSize());

		List<RaceSkilltreeElement> elements = skilltree.getElements();
		if (skilltree.getMenuSlot() >= 0)
			builder.setItem(skilltree.getMenuIcon(), skilltree.getMenuSlot());

		builder.addClickEvent(skilltree.getMenuSlot(), new IMenuClickHandler() {

			@Override
			public void onClick(Player player, ClickType action, ItemStack item) {
				raceManager.getRacesMenu().openRaceMenu(player, race);
			}
		});

		for (RaceSkilltreeElement element : elements) {
			ItemStack elementItem = getElementItem(raceManager, player, element);
			builder.setItem(elementItem, element.getSlot());

			RaceData raceData = racePlayer.getRaceData(race);
			if (raceData.hasPurchasedElement(skilltree.getRegName(), element.getRegName()))
				continue;

			int parentsPurchased = 0;
			for (String parent : element.getParents()) {
				if (raceData.hasPurchasedElement(skilltree.getRegName(), parent))
					parentsPurchased += 1;
			}

			if (parentsPurchased < element.getRequiredParentAmount())
				continue;

			if (racePlayer.getActiveRace() == null || !racePlayer.getActiveRace().equals(race))
				continue;

			builder.addClickEvent(element.getSlot(), new IMenuClickHandler() {

				@Override
				public void onClick(Player player, ClickType action, ItemStack item) {
					ConfirmationMenu menu = new ConfirmationMenu(plugin, new IConfirmationHandler() {

						@Override
						public void onDeny(Player player, ClickType action, ItemStack item) {
							player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("cancelled-purchase", "%prefix% &cCancelled purchase of %element%.").replace("%element%", element.getTitle())));
							openMenu(player);
						}

						@Override
						public void onConfirm(Player player, ClickType action, ItemStack item) {
							if (!raceData.hasPurchasedElement(skilltree.getRegName(), element.getRegName())) {
								if (raceData.getUnusedSkillpoints() < element.getCost()) {
									MessageUtil.sendMessage(player, "invalid-skillpoints", "%prefix% &cYou do not have the required skillpoints to purchase this upgrade!");
									player.closeInventory();
									return;
								}

								RaceElementPurchaseEvent event = new RaceElementPurchaseEvent(player, race, element);
								Bukkit.getPluginManager().callEvent(event);
								if (event.isCancelled())
									return;

								raceData.setUsedSkillpoints(raceData.getUsedSkillpoints() + element.getCost());
								raceData.setUnusedSkillpoints(raceData.getUnusedSkillpoints() - element.getCost());
								raceData.addPurchasedElement(skilltree.getRegName(), element.getRegName());

								player.sendMessage(MessageUtil.getPlaceholderMessage(player, MessageUtil.getMessage("successful-purchase", "%prefix% You have successfully purchased the %element% upgrade for the %skilltree% skilltree!").replace("%element%", element.getTitle()).replace("%skilltree%", skilltree.getName())));
								player.playSound(player.getLocation(), RaceSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1f, 1f);
								openMenu(player);
							} else {
								MessageUtil.sendMessage(player, "already-purchased", "%prefix% &cYou have already purchased this upgrade.");
								player.closeInventory();
							}
						}
					});

					menu.open(player);
				}
			});
		}

		builder.open(player);
	}

	public ItemStack getElementItem(RaceManager raceManager, Player player, RaceSkilltreeElement element) {
		RaceData data = raceManager.getPlayerData(player, race);

		List<String> lore = new ArrayList<String>();
		lore.addAll(element.getDescription());

		if (data.hasPurchasedElement(skilltree.getRegName(), element.getRegName())) {
			if (element.getPurchasedIcon() != null)
				return element.getPurchasedIcon();

			return new ItemBuilder(RaceMaterial.LIME_STAINED_GLASS_PANE.parseItem()).setName(ChatColor.WHITE + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.GREEN + "Purchased").setLore(element.getDescription()).toItemStack();
		}

		int parentsPurchased = 0;
		List<String> parents = new ArrayList<String>(element.getParents());
		for (String parent : element.getParents()) {
			if (parent.contains(":")) {
				String[] split = parent.split(":");
				if (data.hasPurchasedElement(split[0], split[1])) {
					parents.remove(parent);
					parentsPurchased += 1;
				}

			} else {
				if (data.hasPurchasedElement(skilltree.getRegName(), parent)) {
					parents.remove(parent);
					parentsPurchased += 1;
				}
			}
		}

		if (parentsPurchased >= element.getRequiredParentAmount()) {
			if (element.getIcon() != null)
				return element.getIcon();

			lore.add(ChatColor.GRAY + "Skillpoint Cost: " + ChatColor.GREEN + element.getCost());
			lore.add(ChatColor.YELLOW + "Click to purchase.");
			return new ItemBuilder(RaceMaterial.ORANGE_STAINED_GLASS_PANE.parseItem()).setName(ChatColor.WHITE + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Unlocked").setLore(lore).toItemStack();
		}

		if (element.getLockedIcon() != null)
			return element.getLockedIcon();

		// If for some reason the required amount of elements is higher than the skilltree parent amount
		String purchaseString = "the previous skill";
		if (parents.size() >= 1)
			purchaseString = parents.get(0);

		lore.add(ChatColor.RED + "You must unlock " + element.getTitle() + ChatColor.RED + " before");
		lore.add(ChatColor.RED + "purchasing this upgrade.");

		ItemBuilder item = new ItemBuilder(RaceMaterial.RED_STAINED_GLASS_PANE.parseItem()).setName(ChatColor.RED + element.getTitle() + ChatColor.GRAY + " | " + ChatColor.DARK_RED + "Locked").setLore(lore);
		return item.toItemStack();
	}
}
