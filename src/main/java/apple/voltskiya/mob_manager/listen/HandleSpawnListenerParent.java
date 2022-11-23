package apple.voltskiya.mob_manager.listen;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface HandleSpawnListenerParent {

    default boolean isOnlyMobs() {
        return false;
    }

    default boolean shouldHandle(Entity event) {
        return true;
    }

    default String getTag() {
        return getPrefix() + getExtensionTag() + "." + getBriefTag();
    }

    static String getPrefix() {
        return "volt.";
    }

    String getBriefTag();

    String getExtensionTag();

    default void tag(Entity entity) {
        entity.removeScoreboardTag(this.getBriefTag());
        entity.addScoreboardTag(this.getTag());
    }

}
