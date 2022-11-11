package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public interface SpawnListener extends HandleSpawnListenerParent {

    default void registerSpawnListener() {
        MMSpawnListener.get().addListener(this);
    }

    void doSpawn(MMSpawned spawned);

    default void doReload(MMSpawned spawned) {
        doSpawn(spawned);
    }

    default void onDeath(MMSpawned spawned, EntityDeathEvent event) {
    }

    default void onDamage(MMSpawned spawned, EntityDamageEvent event) {
    }

    default void disable(MMSpawned spawned) {
    }

    default MMSpawningOrder order() {
        return MMSpawningOrder.NORMAL;
    }

    default MMSpawningPhase getHandleOnPhase() {
        return MMSpawningPhase.MODIFY;
    }

}
