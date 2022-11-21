package apple.voltskiya.mob_manager.mob;

import org.bukkit.entity.Entity;

public interface HasMMSpawnedUtility extends HasEntityUtility {

    MMSpawned getMMSpawned();

    @Override
    default Entity getEntity() {
        return getMMSpawned().getEntity();
    }

    default void setBlocked(boolean blocked) {
        this.getMMSpawned().setBlocked(blocked);
    }
    default boolean isBlocked() {
        return this.getMMSpawned().isBlocked();
    }
}
