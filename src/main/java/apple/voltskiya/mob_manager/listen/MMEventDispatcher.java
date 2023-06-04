package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.listen.respawn.MMReSpawnResult;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import com.google.common.collect.HashMultimap;
import java.util.Arrays;
import java.util.Set;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MMEventDispatcher {

    private static MMEventDispatcher instance;

    private final HashMultimap<String, ReSpawnListener> respawnListeners = HashMultimap.create();
    private final SpawnListenerList[] spawnListeners = new SpawnListenerList[MMSpawningPhase.values().length];

    public MMEventDispatcher() {
        instance = this;
        Arrays.setAll(spawnListeners, SpawnListenerList::new);
    }

    public static MMEventDispatcher get() {
        return instance;
    }

    public void addListener(SpawnListener listener) {
        int phase = listener.getHandleOnPhase().ordinal();
        Set<SpawnListener> listenersWithTag = spawnListeners[phase].listeners.get(listener.getTag());
        listenersWithTag.add(listener);
    }

    public void addRespawnListener(ReSpawnListener listener) {
        Set<ReSpawnListener> listenersWithTag = respawnListeners.get(listener.getTag());
        listenersWithTag.add(listener);
    }


    private void addModifiers(Entity entity, MMSpawned spawned, SpawnListenerList handler) {
        if (handler.getState() == MMSpawningPhase.INITIALIZE)
            return;
        boolean isMob = entity instanceof Mob;
        for (String tag : entity.getScoreboardTags()) {
            Set<SpawnListener> listenersWithTag = handler.listeners.get(addPrefix(tag));
            for (SpawnListener listener : listenersWithTag) {
                if (listener.isOnlyMobs() && !isMob)
                    continue;
                if (!listener.shouldHandle(entity))
                    continue;
                spawned.getEvents().addHandler(listener, handler.getState());
            }
        }
    }

    private String addPrefix(String tag) {
        if (tag.startsWith(HandleSpawnListenerParent.getPrefix()))
            return tag;
        return HandleSpawnListenerParent.getPrefix() + tag;
    }

    private boolean checkReSpawn(Entity entity) {
        if (MMTagUtils.isRespawned(entity)) {
            MMTagUtils.removeRespawned(entity);
            return false;
        }
        boolean isMob = entity instanceof Mob;
        for (String briefTag : entity.getScoreboardTags()) {
            @NotNull Set<ReSpawnListener> listenersWithTag = respawnListeners.get(addPrefix(briefTag));
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
