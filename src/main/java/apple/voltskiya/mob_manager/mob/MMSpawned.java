package apple.voltskiya.mob_manager.mob;

import apple.voltskiya.mob_manager.listen.MMSpawningPhase;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.storage.MMSpawnedDatabase;
import apple.voltskiya.mob_manager.storage.MMSpawnedSaved;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class MMSpawned {

    private final LivingEntity entity;
    private final MMSpawnHandler[] handlers = new MMSpawnHandler[MMSpawningPhase.values().length];

    public MMSpawned(@NotNull LivingEntity entity) {
        this.entity = entity;
        MMSpawnedRuntimeDatabase.addMob(this);
    }

    public void addHandler(SpawnListener listener, MMSpawningPhase state) {
        boolean shouldAdd = !listener.isOnlyMobs() || this.isMob();
        if (shouldAdd) {
            MMSpawnHandler handler = this.handlers[state.ordinal()];
            if (handler == null)
                handler = this.handlers[state.ordinal()] = new MMSpawnHandler();
            handler.add(listener);
        }
    }

    public void doInitialize() {
        MMSpawnHandler handler = handlers[0];
        if (handler != null)
            handler.handle(this);
    }

    public void doHandle() {
        doToEachHandler(MMSpawnHandler::handle);
        MMTagUtils.setComplete(this.getEntity());
    }


    public void doDeath(EntityDeathEvent event) {
        doToEachHandler(MMSpawnHandler::onDeath, event);
    }

    public void doDamage(EntityDamageEvent event) {
        doToEachHandler(MMSpawnHandler::onDamage, event);
    }

    public void doDisable() {
        doToEachHandler(MMSpawnHandler::disable);
    }

    private <T> void doToEachHandler(Handle<T> action, T event) {
        for (int i = 1; i < handlers.length; i++) {
            MMSpawnHandler handler = handlers[i];
            if (handler != null)
                action.doThing(handler, this, event);
        }
    }

    private void doToEachHandler(HandleEmpty action) {
        for (int i = 1; i < handlers.length; i++) {
            MMSpawnHandler handler = handlers[i];
            if (handler != null)
                action.doThing(handler, this);
        }
    }

    public void save() {
        List<String> listeners = new ArrayList<>();
        for (MMSpawnHandler handler : this.handlers) {
            if (handler != null)
                listeners.addAll(
                    handler.listeners.stream().map(SpawnListener::getBriefTag).toList());
        }
        MMSpawnedSaved saved = new MMSpawnedSaved(this.entity.getUniqueId(), listeners);
        MMSpawnedDatabase.saveMob(saved);
    }

    public boolean isMob() {
        return entity instanceof Mob;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public Mob getMob() {
        return (Mob) this.entity;
    }

    public net.minecraft.world.entity.LivingEntity getNmsEntity() {
        return ((CraftLivingEntity) this.entity).getHandle();
    }

    public net.minecraft.world.entity.Mob getNmsMob() {
        return ((CraftMob) this.entity).getHandle();
    }

    public UUID getUUID() {
        return entity.getUniqueId();
    }

    public boolean isDead() {
        return getEntity().isDead();
    }

    private interface Handle<T> {

        void doThing(MMSpawnHandler handler, MMSpawned mmSpawned, T event);
    }

    private interface HandleEmpty {

        void doThing(MMSpawnHandler handler, MMSpawned mmSpawned);
    }

    private static class MMSpawnHandler {

        private final List<SpawnListener> listeners = new ArrayList<>();

        public void add(SpawnListener listener) {
            this.listeners.add(listener);
            listeners.sort(Comparator.comparingInt((handler) -> handler.order().order()));
        }

        public void handle(MMSpawned spawned) {
            for (SpawnListener listener : listeners) {
                listener.doSpawn(spawned);
            }
        }

        public void disable(MMSpawned spawned) {
            for (SpawnListener listener : listeners) {
                listener.disable(spawned);
            }
        }

        public void onDeath(MMSpawned spawned, EntityDeathEvent event) {
            for (SpawnListener listener : listeners) {
                listener.onDeath(spawned, event);
            }
        }

        public void onDamage(MMSpawned spawned, EntityDamageEvent event) {
            for (SpawnListener listener : listeners) {
                listener.onDamage(spawned, event);
            }
        }
    }
}
