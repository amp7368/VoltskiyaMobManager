package apple.voltskiya.mob_manager;

import apple.voltskiya.mob_manager.listen.MMBukkitEntityListener;
import apple.voltskiya.mob_manager.listen.MMEventDispatcher;
import apple.voltskiya.mob_manager.listen.MMUnloadListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.storage.MMReload;
import apple.voltskiya.mob_manager.storage.MMRuntimeDatabase;
import apple.voltskiya.mob_manager.storage.MMSpawnedStorage;
import com.voltskiya.lib.AbstractModule;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MobManager extends AbstractModule {

    private static MobManager instance;

    public MobManager() {
        instance = this;
    }

    public static MobManager get() {
        return instance;
    }

    @Nullable
    public static MMSpawned getMob(UUID uuid) {
        return MMRuntimeDatabase.getMob(uuid);
    }

    @Override
    public void init() {
        MMSpawnedStorage.loadDatabase();
        new MMEventDispatcher();
    }

    @Override
    public void enable() {
        new MMBukkitEntityListener();
        new MMUnloadListener();
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(MMSpawnedStorage::loadAllMobs);
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(MMReload::reload);
    }

    @Override
    public void onDisable() {
        MMRuntimeDatabase.disableAllMobs();
    }

    @Override
    public String getName() {
        return "MobManager";
    }

}
