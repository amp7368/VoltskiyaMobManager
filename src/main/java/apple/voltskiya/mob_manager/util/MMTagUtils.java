package apple.voltskiya.mob_manager.util;

import org.bukkit.entity.Entity;

public interface MMTagUtils {

    String VOLT_RESPAWNED_TAG = "volt.spawn.respawned";

    static boolean isRespawned(Entity entity) {
        return entity.getScoreboardTags().contains(VOLT_RESPAWNED_TAG);
    }

    static void removeRespawned(Entity entity) {
        entity.removeScoreboardTag(VOLT_RESPAWNED_TAG);
    }

    static void setRespawned(Entity entity) {
        entity.addScoreboardTag(VOLT_RESPAWNED_TAG);
    }
}
