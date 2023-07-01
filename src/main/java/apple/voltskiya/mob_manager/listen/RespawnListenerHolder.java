package apple.voltskiya.mob_manager.listen;

import java.util.Collection;

public interface RespawnListenerHolder {

    Collection<ReSpawnListener> getListeners();

    default void registerListeners() {
        getListeners().forEach(ReSpawnListener::registerReSpawnListener);
    }
}
