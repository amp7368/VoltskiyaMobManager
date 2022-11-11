package apple.voltskiya.mob_manager.storage;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import apple.utilities.threading.service.base.create.AsyncTaskQueueStart;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.threading.service.queue.TaskHandlerQueue;
import apple.voltskiya.mob_manager.MobManager;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class MMSpawnedDatabase implements SaveFileable {

    private static final Map<String, MMSpawnedDatabase> databases = new HashMap<>();
    private static AppleAJDTyped<MMSpawnedDatabase, AsyncTaskQueue> databaseManager;

    private final String id;
    private final Set<MMSpawnedSaved> spawned = new HashSet<>();

    public MMSpawnedDatabase(String id) {
        this.id = id;
    }

    public static void loadDatabase() {
        File saveFolder = MobManager.get().getFile("mobs");
        AsyncTaskQueueStart<AsyncTaskQueue> service = new TaskHandlerQueue(10, 0, 0).taskCreator();
        databaseManager = AppleAJD.createTyped(MMSpawnedDatabase.class, saveFolder, service);
        @NotNull Collection<MMSpawnedDatabase> loaded = databaseManager.loadFolderNow();
        for (MMSpawnedDatabase database : loaded) {
            databases.put(database.id, database);
        }
    }

    public static void saveMob(MMSpawnedSaved saved) {
        String id = getId(saved.getUUID());
        databases.computeIfAbsent(id, MMSpawnedDatabase::new).doSaveMob(saved);
    }

    private static String getId(UUID uuid) {
        return uuid.toString().substring(0, 2);
    }

    public static void loadAllMobs() {
        for (MMSpawnedDatabase database : databases.values()) {
            database.loadMobs();
        }
    }

    private void doSaveMob(MMSpawnedSaved saved) {
        this.spawned.add(saved);
        save();
    }

    private void save() {
        databaseManager.saveInFolder(this);
    }

    private void loadMobs() {
        for (Iterator<MMSpawnedSaved> iterator = spawned.iterator(); iterator.hasNext(); ) {
            MMSpawnedSaved mob = iterator.next();
            if (mob.isDead()) {
                iterator.remove();
            } else {
                mob.doLoad();
            }
        }
        save();
    }

    @Override
    public String getSaveFileName() {
        return extensionJson(id);
    }
}
