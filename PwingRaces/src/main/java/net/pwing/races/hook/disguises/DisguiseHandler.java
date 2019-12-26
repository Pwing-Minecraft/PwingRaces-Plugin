package net.pwing.races.hook.disguises;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DisguiseHandler {

    public void disguiseEntity(Entity entity, EntityType type) {
        MobDisguise mob = new MobDisguise(DisguiseType.getType(type));
        DisguiseAPI.disguiseEntity(entity, mob);
    }

    public void undisguiseEntity(Entity entity) {
        DisguiseAPI.undisguiseToAll(entity);
    }
}
