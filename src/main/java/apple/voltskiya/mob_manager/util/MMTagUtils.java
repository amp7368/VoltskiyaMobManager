package apple.voltskiya.mob_manager.util;

import org.bukkit.entity.Entity;

public interface MMTagUtils {

    String VOLT_RESPAWNED_TAG = "volt.spawn.respawned";
    String VOLT_SPAWN_COMPLETE_TAG = "volt.spawn.complete";


    static boolean isComplete(Entity entity) {
        return entity.getScoreboardTags().contains(VOLT_SPAWN_COMPLETE_TAG);
    }

    static void removeComplete(Entity entity) {
        entity.removeScoreboardTag(VOLT_SPAWN_COMPLETE_TAG);
    }

    static void setComplete(Entity entity) {
        entity.addScoreboardTag(VOLT_SPAWN_COMPLETE_TAG);
    }

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
