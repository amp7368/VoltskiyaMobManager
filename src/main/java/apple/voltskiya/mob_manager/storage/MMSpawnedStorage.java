package apple.voltskiya.mob_manager.storage;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import apple.utilities.threading.service.base.create.AsyncTaskQueueStart;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.threading.service.queue.TaskHandlerQueue;
import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.listen.MMUnloadListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MMSpawnedStorage implements SaveFileable {

    private static final Map<String, MMSpawnedStorage> databases = new HashMap<>();
    private static AppleAJDTyped<MMSpawnedStorage> databaseManager;
    private final Map<UUID, MMSpawnedSaved> spawned = new HashMap<>();
    private String id;

    public MMSpawnedStorage(String id) {
        this.id = id;
    }

    public MMSpawnedStorage() {
    }

    public static void loadDatabase() {
        MobManager.get().logger().info("Loading MMSpawnedStorage database");
        File saveFolder = MobManager.get().getFile("Mobs");
        AsyncTaskQueueStart<AsyncTaskQueue> service = new TaskHandlerQueue(10, 0, 0).taskCreator();
        databaseManager = AppleAJD.createTyped(MMSpawnedStorage.class, saveFolder, service);
        @NotNull Collection<MMSpawnedStorage> loaded = databaseManager.loadFolderNow();
        for (MMSpawnedStorage database : loaded) {
            databases.put(database.id, database);
        }
        MobManager.get().logger().info("Loaded MMSpawnedStorage database");
    }

    private static String getId(UUID uuid) {
        return uuid.toString().substring(0, 2);
    }

    public static void saveMob(UUID uuid, MMSpawnedSaved saved) {
        String id = getId(uuid);
        databases.computeIfAbsent(id, MMSpawnedStorage::new).doSaveMob(uuid, saved);
    }

    public static void loadAllMobs() {
        MobManager.get().logger().info("Loading MMSpawnedStorage Mobs");
        databases.values().forEach(MMSpawnedStorage::loadMobs);
        MobManager.get().logger().info("Loaded MMSpawnedStorage Mobs");
    }

    private void doSaveMob(UUID uuid, MMSpawnedSaved saved) {
        this.spawned.put(uuid, saved);
        saveDatabase();
    }

    private void saveDatabase() {
        databaseManager.saveInFolder(this);
    }

    private void loadMobs() {
        for (Iterator<Entry<UUID, MMSpawnedSaved>> iterator = spawned.entrySet().iterator();
            iterator.hasNext(); ) {
            Entity entity = Bukkit.getEntity(iterator.next().getKey());
            if (entity == null) {
                iterator.remove();
                continue;
            }
            MMUnloadListener.load(entity);
        }
        saveDatabase();
    }

    @Override
    public String getSaveFileName() {
        return extensionJson(id);
    }
}
