package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.MMSpawnedRuntimeDatabase;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import com.google.common.collect.HashMultimap;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMSpawnListener implements Listener {

    private static MMSpawnListener instance;

    private final HashMultimap<String, ReSpawnListener> respawnListeners = HashMultimap.create();
    private final SpawnHandlerListeners[] spawnListeners = new SpawnHandlerListeners[MMSpawningPhase.values().length];

    public MMSpawnListener() {
        instance = this;
        Arrays.setAll(spawnListeners, SpawnHandlerListeners::new);
    }

    public static MMSpawnListener get() {
        return instance;
    }

    public void addListener(SpawnListener listener) {
        int phase = listener.getHandleOnPhase().ordinal();
        Set<SpawnListener> listenersWithTag = spawnListeners[phase].listeners.get(
            listener.getBriefTag());
        listenersWithTag.add(listener);
    }

    public void addRespawnListener(ReSpawnListener listener) {
        Set<ReSpawnListener> listenersWithTag = respawnListeners.get(listener.getBriefTag());
        listenersWithTag.add(listener);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(CreatureSpawnEvent event) {
        if (MMTagUtils.isComplete(event.getEntity()))
            return;
        // do the initial sweep to check if the mob will redo the spawn
        if (checkReSpawn(event))
            return;

        // create the mob
        MMSpawned spawned = new MMSpawned(event.getEntity());

        // add spawn listeners
        for (SpawnHandlerListeners handlerListener : spawnListeners) {
            addModifiers(event, spawned, handlerListener);
        }

        // do the listeners
        spawned.doInitialize();
        schedule(spawned::doHandle);
        spawned.save();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(EntityDeathEvent event) {
        @Nullable MMSpawned mob = MMSpawnedRuntimeDatabase.getMob(event.getEntity().getUniqueId());
        if (mob != null) {
            mob.doDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageEvent event) {
        @Nullable MMSpawned mob = MMSpawnedRuntimeDatabase.getMob(event.getEntity().getUniqueId());
        if (mob != null) {
            mob.doDamage(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(EntityDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        @Nullable MMSpawned mob = MMSpawnedRuntimeDatabase.getMob(uuid);
        if (mob != null) {
            mob.doDisable();
            schedule(() -> MMSpawnedRuntimeDatabase.checkRemoveMob(uuid));
        }
    }

    private void addModifiers(CreatureSpawnEvent event, MMSpawned spawned,
        SpawnHandlerListeners handler) {
        boolean isCancelled = event.isCancelled();
        boolean isMob = event.getEntity() instanceof Mob;
        for (String tag : event.getEntity().getScoreboardTags()) {
            Set<SpawnListener> listenersWithTag = handler.listeners.get(tag);
            if (handler.getState() == MMSpawningPhase.INITIALIZE)
                continue;
            for (SpawnListener listener : listenersWithTag) {
                if (isCancelled && listener.ignoreCancelled())
                    continue;
                if (listener.isOnlyMobs() && !isMob)
                    continue;
                if (!listener.shouldHandle(event))
                    continue;
                spawned.addHandler(listener, handler.getState());
            }
        }
    }

    private boolean checkReSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (MMTagUtils.isRespawned(entity))
            return false;
        boolean eventIsCancelled = event.isCancelled();
        boolean isMob = entity instanceof Mob;
        for (String tag : entity.getScoreboardTags()) {
            String briefTag = MMTagUtils.getBriefTag(tag);
            @NotNull Set<ReSpawnListener> listenersWithTag = respawnListeners.get(briefTag);
            for (ReSpawnListener listener : listenersWithTag) {
                if (eventIsCancelled && listener.ignoreCancelled())
                    continue;
                if (listener.isOnlyMobs() && !isMob)
                    continue;
                if (!listener.shouldHandle(event))
                    continue;
                event.setCancelled(true);
                schedule(() -> doRespawn(event, listener));
                return true;
            }
        }
        return false;
    }


    private void doRespawn(CreatureSpawnEvent event, ReSpawnListener listener) {
        MMReSpawnResult result = listener.doReSpawn(event);
        @Nullable Entity entity = result.entity();
        if (entity == null) {
            String warning = String.format("%s failed to be created at %s", listener.getBriefTag(),
                event.getLocation());
            MMVoltskiyaPlugin.get().getLogger().log(Level.WARNING, warning);
            return;
        }
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        MMTagUtils.setRespawned(bukkitEntity);
        bukkitEntity.addScoreboardTag(listener.getVoltTag());
        result.addEntityToWorld();
    }

    public void handle(LivingEntity me, List<String> listeners) {
        MMSpawned spawned = new MMSpawned(me);
        for (SpawnHandlerListeners handler : this.spawnListeners) {
            MMSpawningPhase state = handler.getState();
            for (String listenerName : listeners) {
                Set<SpawnListener> listenersWithName = handler.listeners.get(listenerName);
                for (SpawnListener listener : listenersWithName) {
                    spawned.addHandler(listener, state);
                }
            }
        }
        spawned.doInitialize();
        schedule(spawned::doHandle);
    }

    private void schedule(Runnable runnable) {
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(runnable);
    }

    private static class SpawnHandlerListeners {

        private final HashMultimap<String, SpawnListener> listeners = HashMultimap.create();
        private final MMSpawningPhase state;

        public SpawnHandlerListeners(int index) {
            state = MMSpawningPhase.values()[index];
        }

        public MMSpawningPhase getState() {
            return state;
        }
    }
}
