package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.listen.order.MMSpawningPhase;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.event.MMListener;

public interface SpawnListener extends HandleSpawnListenerParent, MMListener {

    default void registerSpawnListener() {
        MMEventDispatcher.get().addListener(this);
    }

    void doSpawn(MMSpawned spawned);

    @Override
    default String getExtensionTag() {
        return "ability";
    }

    default MMSpawningPhase getHandleOnPhase() {
        return MMSpawningPhase.MODIFY;
    }

}
