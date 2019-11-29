package net.pwing.races.race.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.pwing.races.PwingRaces;
import net.pwing.races.api.race.Race;
import net.pwing.races.api.race.RaceData;
import net.pwing.races.api.race.RacePlayer;
import net.pwing.races.api.race.permission.RacePermission;
import net.pwing.races.api.race.permission.RacePermissionManager;
import net.pwing.races.api.race.skilltree.RaceSkilltree;
import net.pwing.races.hooks.VaultAPIHook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PwingRacePermissionManager implements RacePermissionManager {

    private PwingRaces plugin;

    public PwingRacePermissionManager(PwingRaces plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new RacePermissionListener(this), plugin);
    }

    public void applyPermissions(Player player) {
        VaultAPIHook vaultHook = plugin.getVaultHook();
        if (!plugin.getRaceManager().isRacesEnabledInWorld(player.getWorld())) {
            for (RacePermission perm : getApplicablePermissions(player)) {
                if (!vaultHook.playerHasPermission(player, perm.getNode()))
                    continue;

                vaultHook.removePermission(player, perm.getNode());
            }

            return;
        }

        Collection<RacePermission> racePermissions = getApplicablePermissions(player);
        if (racePermissions == null || racePermissions.isEmpty())
            return;

        for (RacePermission perm : racePermissions) {
            if (perm.getNode().startsWith("^"))
                vaultHook.removePermission(player, perm.getNode().replace("^", ""));
            else
                vaultHook.addPermission(player, perm.getNode());

        }
    }

    public Collection<RacePermission> getApplicablePermissions(Player player) {
        RacePlayer racePlayer = plugin.getRaceManager().getRacePlayer(player);
        if (racePlayer == null)
            return new ArrayList<>();

        if (!racePlayer.getRace().isPresent())
            return new ArrayList<>();

        Race race = racePlayer.getRace().get();
        RaceData data = racePlayer.getRaceData(race);

        List<RacePermission> permissions = new ArrayList<>();
        for (String key : race.getRacePermissionsMap().keySet()) {
            List<RacePermission> definedPermissions = race.getRacePermissionsMap().get(key);

            for (RacePermission definedPermission : definedPermissions) {
                String req = definedPermission.getRequirement();

                if (req.equals("none")) {
                    permissions.add(definedPermission);

                } else if (req.startsWith("level")) { // best to assume it's a level-based permission
                    int level = Integer.parseInt(req.replace("level", ""));

                    if (data.getLevel() < level)
                        continue;

                    permissions.add(definedPermission);
                } else {
                    for (RaceSkilltree skillTree : plugin.getRaceManager().getSkilltreeManager().getSkilltrees()) {
                        if (data.hasPurchasedElement(skillTree.getInternalName(), req)) {
                            permissions.add(definedPermission);
                        }
                    }
                }
            }
        }

        return permissions;
    }
}
