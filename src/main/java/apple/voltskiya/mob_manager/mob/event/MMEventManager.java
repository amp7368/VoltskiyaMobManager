package apple.voltskiya.mob_manager.mob.event;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.storage.MMSpawnedSaved;
import apple.voltskiya.mob_manager.storage.MMSpawnedStorage;
import apple.voltskiya.mob_manager.util.MMTagUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class MMEventManager implements HasMMSpawnedUtility {

    private final MMSpawnHandler[] handlers = new MMSpawnHandler[MMSpawningPhase.values().length];
    private final MMSpawned mob;

    public MMEventManager(MMSpawned mob) {
        this.mob = mob;
    }

    public void addHandler(SpawnListener listener, MMSpawningPhase state) {
        boolean shouldAdd = !listener.isOnlyMobs() || this.mob.isMob();
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
            handler.handle(this.mob);
    }

    public void doHandle() {
        doToEachHandler(MMSpawnHandler::tag);
        doToEachHandler(MMSpawnHandler::handle);
        MMTagUtils.setComplete(this.mob.getEntity());
    }

    public void save() {
        List<String> listeners = new ArrayList<>();
        for (MMSpawnHandler handler : this.handlers) {
            if (handler != null)
                listeners.addAll(
                    handler.listeners.stream().map(SpawnListener::getBriefTag).toList());
        }
        MMSpawnedSaved saved = new MMSpawnedSaved(listeners);
        MMSpawnedStorage.saveMob(this.mob.getUUID(), saved);
    }

    private <T> void doToEachHandler(Handle<T> action, T event) {
        for (int i = 1; i < handlers.length; i++) {
            MMSpawnHandler handler = handlers[i];
            if (handler != null)
                action.doThing(handler, this.mob, event);
        }
    }

    private void doToEachHandler(HandleEmpty action) {
        for (int i = 1; i < handlers.length; i++) {
            MMSpawnHandler handler = handlers[i];
            if (handler != null)
                action.doThing(handler, this.mob);
        }
    }


    public void doDeath(EntityDeathEvent event) {
        doToEachHandler(MMSpawnHandler::onDeath, event);
    }

    public void doDamage(EntityDamageEvent event) {
        doToEachHandler(MMSpawnHandler::onDamage, event);
    }

    public void disable() {
        doToEachHandler(MMSpawnHandler::disable);
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
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

        public void tag(MMSpawned mob) {
            Entity entity = mob.getEntity();
            for (SpawnListener listener : listeners) {
                listener.tag(entity);
            }
        }
    }
}
