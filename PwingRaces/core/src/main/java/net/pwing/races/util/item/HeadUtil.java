package net.pwing.races.util.item;

import net.pwing.races.PwingRaces;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeadUtil {
    private static final Map<String, PlayerProfile> CACHED_HEADS = new HashMap<>();

    public static ItemStack getPlayerHead(ItemStack item, String headOwner) {
        PlayerProfile cachedProfile = CACHED_HEADS.get(headOwner);
        if (cachedProfile != null) {
            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
            itemMeta.setOwnerProfile(cachedProfile);
            item.setItemMeta(itemMeta);
            return item;
        }

        CompletableFuture.supplyAsync(() -> Bukkit.createPlayerProfile(headOwner)).thenComposeAsync(PlayerProfile::update).whenComplete((profile, ex) -> {
            if (ex != null) {
                PwingRaces.getInstance().getLogger().warning("Failed to complete profile for head owner " + headOwner + "!");
                return;
            }

            Bukkit.getScheduler().runTask(PwingRaces.getInstance(), () -> {
                CACHED_HEADS.put(headOwner, profile);

                SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
                itemMeta.setOwnerProfile(profile);
                item.setItemMeta(itemMeta);
            });
        });

        return item;
    }
}
