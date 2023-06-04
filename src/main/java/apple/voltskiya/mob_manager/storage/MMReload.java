package apple.voltskiya.mob_manager.storage;

import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MMReload {

    public static void reload() {
        MobManager.get().logger().info("Loading MMReload chunks");
        for (World world : Bukkit.getWorlds()) {
            loadEntities(world.getEntities());
        }
        MobManager.get().logger().info("Loaded chunks");
    }

    private static void loadEntities(@NotNull List<Entity> entities) {
        for (Entity entity : entities) {
            if (MMTagUtils.isRespawned(entity))
                reloadEntity(entity);

        }
    }

    private static void reloadEntity(Entity entity) {
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
