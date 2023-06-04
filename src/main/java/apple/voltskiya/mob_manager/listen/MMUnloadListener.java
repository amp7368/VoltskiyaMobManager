package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.storage.MMRuntimeDatabase;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jetbrains.annotations.Nullable;

public class MMUnloadListener implements Listener {

    public MMUnloadListener() {
        MMVoltskiyaPlugin.get().registerEvents(this);
    }

    public static void unload(Entity entity) {
        UUID uuid = entity.getUniqueId();
        @Nullable MMSpawned mob = MMRuntimeDatabase.getMob(uuid);
        if (mob == null)
            return;
        mob.disable();
        MMRuntimeDatabase.removeMob(uuid);
        MMTagUtils.removeRespawned(entity);
    }

    public static void load(Entity entity) {
        if (entity.isDead())
            return;
        UUID uuid = entity.getUniqueId();
        if (MMRuntimeDatabase.hasMob(uuid))
            return;
        MMEventDispatcher.get().load(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void unload(EntitiesUnloadEvent event) {
        event.getEntities().forEach(MMUnloadListener::unload);
    }

    @EventHandler(ignoreCancelled = true)
    public void load(EntitiesLoadEvent event) {
        event.getEntities().forEach(MMUnloadListener::load);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(EntityDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        @Nullable MMSpawned mob = MMRuntimeDatabase.getMob(uuid);
        if (mob != null)
            mob.onDeath(event);
        unload(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(CreatureSpawnEvent event) {
        load(event.getEntity());
    }
}
