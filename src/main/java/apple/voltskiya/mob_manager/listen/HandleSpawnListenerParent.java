package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.util.MMTagUtils;
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

    String getBriefTag();

    default String getVoltTag() {
        return MMTagUtils.getBriefTag(getBriefTag());
    }
}
