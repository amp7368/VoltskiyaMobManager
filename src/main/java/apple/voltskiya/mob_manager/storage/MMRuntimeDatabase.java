package apple.voltskiya.mob_manager.storage;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.Nullable;

public class MMRuntimeDatabase {

    public static final Map<UUID, MMSpawned> mobs = new HashMap<>();

    public static void addMob(MMSpawned mob) {
        MMSpawned oldMob;
        synchronized (mobs) {
            oldMob = mobs.put(mob.getUUID(), mob);
        }
        if (oldMob != null)
            oldMob.disable();
    }

    public static void disableAllMobs() {
        synchronized (mobs) {
            for (MMSpawned mob : mobs.values()) {
                mob.disable();
            }
            mobs.clear();
        }
    }

    @Nullable
    public static MMSpawned getMob(UUID uuid) {
        synchronized (mobs) {
            return mobs.get(uuid);
        }
    }

    public static void removeMob(UUID uuid) {
        synchronized (mobs) {
            mobs.remove(uuid);
        }
    }


    public static boolean hasMob(UUID uuid) {
        synchronized (mobs) {
            return mobs.containsKey(uuid);
        }
    }
}
