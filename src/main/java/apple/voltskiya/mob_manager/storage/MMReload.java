package apple.voltskiya.mob_manager.storage;

import apple.voltskiya.mob_manager.util.MMTagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class MMReload {

    public static void reload() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                reloadEntity(entity);
            }
        }
    }

    private static void reloadEntity(Entity entity) {
        if (MMTagUtils.isRespawned(entity)) {
            MMTagUtils.removeComplete(entity);
            MMTagUtils.removeRespawned(entity);
            net.minecraft.world.entity.Entity handle = ((CraftEntity) entity).getHandle();
            Level world = handle.getLevel();
            net.minecraft.world.entity.Entity newEntity = handle.getType().create(world);
            if (newEntity == null)
                return;
            CompoundTag nbt = new CompoundTag();
            handle.save(nbt);
            newEntity.load(nbt);
            entity.remove();
            world.addFreshEntity(newEntity, entity.getEntitySpawnReason());
        }
    }
}
