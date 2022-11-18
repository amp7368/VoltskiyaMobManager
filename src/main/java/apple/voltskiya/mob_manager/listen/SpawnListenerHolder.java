package apple.voltskiya.mob_manager.listen;

import java.util.Collection;

public interface SpawnListenerHolder {

    Collection<SpawnListener> getListeners();

    default void registerListeners() {
        getListeners().forEach(SpawnListener::registerSpawnListener);
    }
}

