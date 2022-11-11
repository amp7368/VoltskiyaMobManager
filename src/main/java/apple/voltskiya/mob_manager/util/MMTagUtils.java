package apple.voltskiya.mob_manager.util;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface MMTagUtils {

    String VOLT_RESPAWNED_TAG = "volt.spawn.respawned";
    String VOLT_SPAWN_COMPLETE_TAG = "volt.spawn.complete";
    String VOLT_TAG = "volt.mob.";

    @NotNull
    static String getBriefTag(String tag) {
        if (!isVoltTag(tag))
            return tag;
        if (tag.length() <= VOLT_TAG.length())
            return "null";// return a string with the contents of null, to tell the user something is wrong
        return tag.substring(VOLT_TAG.length());
    }

    static String getVoltTag(String tag) {
        if (isVoltTag(tag))
            return tag;
        return VOLT_TAG + tag;
    }

    private static boolean isVoltTag(String tag) {
        return tag.startsWith(VOLT_TAG);
    }

    static boolean isComplete(Entity entity) {
        return entity.getScoreboardTags().contains(VOLT_SPAWN_COMPLETE_TAG);
    }

    static void removeComplete(Entity entity) {
        entity.removeScoreboardTag(VOLT_SPAWN_COMPLETE_TAG);
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

    static void setComplete(Entity entity) {
        entity.addScoreboardTag(VOLT_SPAWN_COMPLETE_TAG);
    }


}
