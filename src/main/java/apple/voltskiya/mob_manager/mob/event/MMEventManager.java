package apple.voltskiya.mob_manager.mob.event;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.storage.MMSpawnedSaved;
import apple.voltskiya.mob_manager.storage.MMSpawnedStorage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class MMEventManager implements HasMMSpawnedUtility, IHasMMListener {

    private final MMSpawnHandler[] handlers = new MMSpawnHandler[MMSpawningPhase.values().length];
    private final MMSpawned mob;
    private final List<IMMListener> listeners = new ArrayList<>();

    public MMEventManager(MMSpawned mob) {
        this.mob = mob;
    }

    public void addListener(IMMListener listener) {
        this.listeners.add(listener);
        this.listeners.sort(Comparator.comparingInt(IMMListener::orderValue));
    }

    public void removeListener(IMMListener listener) {
        this.listeners.remove(listener);
    }

    public void addHandler(SpawnListener listener, MMSpawningPhase state) {
        boolean shouldAdd = !listener.isOnlyMobs() || this.mob.isMob();
        if (shouldAdd) {
            this.addListener(listener);
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
    }

    public void save() {
        List<String> listeners = new ArrayList<>();
        for (MMSpawnHandler handler : this.handlers) {
            if (handler != null)
                listeners.addAll(
                    handler.listeners.stream().map(SpawnListener::getTag).toList());
        }
        MMSpawnedSaved saved = new MMSpawnedSaved(listeners);
        MMSpawnedStorage.saveMob(this.mob.getUUID(), saved);
    }

    private void doToEachHandler(HandleEmpty action) {
        for (int i = 1; i < handlers.length; i++) {
            MMSpawnHandler handler = handlers[i];
            if (handler != null)
                action.doThing(handler, this.mob);
        }
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    @Override
    public List<IMMListener> listeners() {
        return List.copyOf(this.listeners);
    }

    private interface HandleEmpty {

        void doThing(MMSpawnHandler handler, MMSpawned mmSpawned);
    }

    private static class MMSpawnHandler implements IMMListener {

        private final List<SpawnListener> listeners = new ArrayList<>();

        public void add(SpawnListener listener) {
            this.listeners.add(listener);
            this.listeners.sort(Comparator.comparingInt(SpawnListener::orderValue));
        }

        public void handle(MMSpawned spawned) {
            for (SpawnListener listener : listeners) {
                listener.doSpawn(spawned);
            }
        }

        @Override
        public void disable(MMSpawned spawned) {
            for (SpawnListener listener : listeners) {
                listener.disable(spawned);
            }
        }

        @Override
        public void onDeath(MMSpawned spawned, EntityDeathEvent event) {
            for (SpawnListener listener : listeners) {
                listener.onDeath(spawned, event);
            }
        }

        @Override
        public void onDamage(MMSpawned spawned, EntityDamageEvent event) {
            for (SpawnListener listener : listeners) {
                listener.onDamage(spawned, event);
            }
        }

        @Override
        public void onTarget(MMSpawned spawned, EntityTargetEvent event) {
            for (SpawnListener listener : listeners) {
                listener.onTarget(spawned, event);
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
