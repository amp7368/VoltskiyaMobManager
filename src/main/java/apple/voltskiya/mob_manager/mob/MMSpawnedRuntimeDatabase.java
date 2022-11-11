package apple.voltskiya.mob_manager.mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MMSpawnedRuntimeDatabase {

    public static final Map<UUID, MMSpawned> mobs = new HashMap<>();

    public static void addMob(MMSpawned mob) {
        MMSpawned oldMob;
        synchronized (mobs) {
            oldMob = mobs.put(mob.getUUID(), mob);
        }
        if (oldMob != null)
            oldMob.doDisable();
    }

    @Nullable
    public static MMSpawned getMob(UUID uuid) {
        synchronized (mobs) {
            return mobs.get(uuid);
        }
    }

    public static void checkRemoveMob(UUID uuid) {
        @Nullable MMSpawned mob = getMob(uuid);
        if (mob != null && mob.isDead()) {
            synchronized (mobs) {
                mobs.remove(uuid);
            }
        }
    }

    public static void disableAllMobs() {
        synchronized (mobs) {
            for (MMSpawned mob : mobs.values()) {
                mob.doDisable();
            }
        }
    }
}
