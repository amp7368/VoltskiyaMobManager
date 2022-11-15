package apple.voltskiya.mob_manager;

import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.MMSpawnedRuntimeDatabase;
import apple.voltskiya.mob_manager.storage.MMReload;
import apple.voltskiya.mob_manager.storage.MMSpawnedDatabase;
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
        return MMSpawnedRuntimeDatabase.getMob(uuid);
    }

    @Override
    public void init() {
        MMSpawnedDatabase.loadDatabase();
        new MMSpawnListener();
    }

    @Override
    public void enable() {
        MMVoltskiyaPlugin.get().registerEvents(MMSpawnListener.get());
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(MMSpawnedDatabase::loadAllMobs);
        MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(MMReload::reload);
    }

    @Override
    public void onDisable() {
        MMSpawnedRuntimeDatabase.disableAllMobs();
    }

    @Override
    public String getName() {
        return "MobManager";
    }

}
