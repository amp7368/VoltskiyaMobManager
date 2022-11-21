package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.listen.order.MMSpawningOrder;
import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public interface SpawnListener extends HandleSpawnListenerParent {

    default void registerSpawnListener() {
        MMEventDispatcher.get().addListener(this);
    }

    void doSpawn(MMSpawned spawned);

    default void onDeath(MMSpawned spawned, EntityDeathEvent event) {
    }

    default void disable(MMSpawned spawned) {
    }

    default void onDamage(MMSpawned spawned, EntityDamageEvent event) {
    }

    @Override
    default String getExtensionTag() {
        return "ability";
    }

    default MMSpawningOrder order() {
        return MMSpawningOrder.NORMAL;
    }

    default MMSpawningPhase getHandleOnPhase() {
        return MMSpawningPhase.MODIFY;
    }

}
