package apple.voltskiya.mob_manager.mob;

import apple.nms.decoding.entity.DecodeEntity;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.Nullable;

public interface HasEntityUtility {


    // Casted entity
    Entity getEntity();

    default net.minecraft.world.entity.Entity getNmsEntity() {
        return ((CraftEntity) getEntity()).getHandle();
    }

    default net.minecraft.world.entity.Mob getNmsMob() {
        return (net.minecraft.world.entity.Mob) getNmsEntity();
    }

    default Mob getMob() {
        return (Mob) getEntity();
    }

    default boolean isDead() {
        return getEntity().isDead();
    }

    default boolean isMob() {
        return getEntity() instanceof Mob;
    }

    default UUID getUUID() {
        return getEntity().getUniqueId();
    }

    // utility about mob
    default Location getLocation() {
        return getEntity().getLocation();
    }

    default Location getEyeLocation() {
        return getMob().getEyeLocation();
    }

    default int getTicksLived() {
        return getEntity().getTicksLived();
    }

    default World getWorld() {
        return getEntity().getWorld();
    }

    default boolean wasHit(int inLast) {
        net.minecraft.world.entity.Mob mob = getNmsMob();
        int hurt = DecodeEntity.getHurtTimestamp(mob);
        int ticksLived = DecodeEntity.getTicksLived(mob);

        return hurt != 0 && hurt + inLast >= ticksLived && getMob().getLastDamage() != 0;
    }

    default @Nullable LivingEntity getTarget() {
        return getMob().getTarget();
    }

    default boolean hasTarget() {
        return getTarget() != null;
    }

}
