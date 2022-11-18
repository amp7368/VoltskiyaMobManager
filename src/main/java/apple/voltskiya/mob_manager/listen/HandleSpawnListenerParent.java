package apple.voltskiya.mob_manager.listen;

import org.bukkit.entity.Entity;

public interface HandleSpawnListenerParent {

    default boolean isOnlyMobs() {
        return false;
    }

    default boolean shouldHandle(Entity event) {
        return true;
    }

    default String getTag() {
        return "volt." + getExtensionTag() + "." + getBriefTag();
    }

    String getBriefTag();

    String getExtensionTag();

    default void tag(Entity entity) {
        entity.removeScoreboardTag(this.getBriefTag());
        entity.addScoreboardTag(this.getTag());
    }
}
