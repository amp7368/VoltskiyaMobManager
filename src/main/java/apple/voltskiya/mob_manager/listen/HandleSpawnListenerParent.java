package apple.voltskiya.mob_manager.listen;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface HandleSpawnListenerParent {

    default boolean ignoreCancelled() {
        return true;
    }

    default boolean isOnlyMobs() {
        return false;
    }

    default boolean shouldHandle(CreatureSpawnEvent event) {
        return true;
    }

    default String getTag() {
        return "volt." + getExtensionTag() + "." + getBriefTag();
    }

    String getBriefTag();

    String getExtensionTag();

    default void tag(Entity entity) {
        entity.addScoreboardTag(this.getTag());
        entity.removeScoreboardTag(this.getBriefTag());
    }
}
