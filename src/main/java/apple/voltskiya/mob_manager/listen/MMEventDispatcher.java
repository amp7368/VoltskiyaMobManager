package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.listen.respawn.MMReSpawnResult;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import com.google.common.collect.HashMultimap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MMEventDispatcher {

    private static MMEventDispatcher instance;

    private final HashMultimap<String, ReSpawnListener> respawnListeners = HashMultimap.create();
    private final SpawnListenerList[] spawnListeners = new SpawnListenerList[MMSpawningPhase.values().length];
    private final Set<String> fullExtensions = new HashSet<>();

    public MMEventDispatcher() {
        instance = this;
        Arrays.setAll(spawnListeners, SpawnListenerList::new);
    }

    public static MMEventDispatcher get() {
        return instance;
    }

    public void addListener(SpawnListener listener) {
        int phase = listener.getHandleOnPhase().ordinal();
        Set<SpawnListener> listenersWithTag = spawnListeners[phase].listeners.get(listener.getBriefTag());
        listenersWithTag.add(listener);
        synchronized (this.fullExtensions) {
            this.fullExtensions.add(listener.getFullExtensionTag());
        }
    }

    public void addRespawnListener(ReSpawnListener listener) {
        Set<ReSpawnListener> listenersWithTag = respawnListeners.get(listener.getBriefTag());
        listenersWithTag.add(listener);
        synchronized (this.fullExtensions) {
            this.fullExtensions.add(listener.getFullExtensionTag());
        }
    }


    private void addModifiers(Entity entity, MMSpawned spawned, SpawnListenerList handler) {
        boolean isMob = entity instanceof Mob;
        for (String tag : List.copyOf(entity.getScoreboardTags())) {
            Set<SpawnListener> listenersWithTag = handler.listeners.get(getBriefTag(tag));
            if (handler.getState() == MMSpawningPhase.INITIALIZE)
                continue;
            for (SpawnListener listener : listenersWithTag) {
                if (listener.isOnlyMobs() && !isMob)
                    continue;
                if (!listener.shouldHandle(entity))
                    continue;
                spawned.getEvents().addHandler(listener, handler.getState());
            }
        }
    }

    private String getBriefTag(String tag) {
        synchronized (this.fullExtensions) {
            for (String fullExtension : this.fullExtensions) {
                if (tag.startsWith(fullExtension))
                    return tag.substring(fullExtension.length());
            }
        }
        return tag;
    }

    private boolean checkReSpawn(Entity entity) {
        if (MMTagUtils.isRespawned(entity))
            return false;
        boolean isMob = entity instanceof Mob;
        for (String briefTag : entity.getScoreboardTags()) {
            @NotNull Set<ReSpawnListener> listenersWithTag = respawnListeners.get(briefTag);
            for (ReSpawnListener listener : listenersWithTag) {
                if (listener.isOnlyMobs() && !isMob)
                    continue;
                if (!listener.shouldHandle(entity))
                    continue;
                entity.remove();
                schedule(() -> doRespawn(entity, listener));
                return true;
            }
        }
        return false;
    }


    private void doRespawn(Entity entity, ReSpawnListener listener) {
        MMReSpawnResult result = listener.doReSpawn(((CraftEntity) entity).getHandle());
        if (result == null) {
            String warning = String.format("%s failed to be created at %s", listener.getBriefTag(), entity.getLocation());
            MobManager.get().logger().warn(warning);
            return;
        }
        Entity bukkitEntity = result.entity().getBukkitEntity();
        bukkitEntity.removeScoreboardTag(listener.getBriefTag());
        bukkitEntity.addScoreboardTag(listener.getTag());
        MMTagUtils.setRespawned(bukkitEntity);
        result.addEntityToWorld();
    }

    private void schedule(Runnable runnable) {
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(runnable);
    }

    public void load(Entity entity) {
        // do the initial sweep to check if the mob will redo the spawn
        if (checkReSpawn(entity))
            return;

        // create the mob
        MMSpawned spawned = new MMSpawned(entity);

        // add spawn listeners
        for (SpawnListenerList handlerListener : spawnListeners) {
            addModifiers(entity, spawned, handlerListener);
        }

        // do the listeners
        spawned.getEvents().doInitialize();
        spawned.getEvents().save();
        schedule(spawned::doHandle);
    }

    private static class SpawnListenerList {

        private final HashMultimap<String, SpawnListener> listeners = HashMultimap.create();
        private final MMSpawningPhase state;

        public SpawnListenerList(int index) {
            state = MMSpawningPhase.values()[index];
        }

        public MMSpawningPhase getState() {
            return state;
        }
    }
}
