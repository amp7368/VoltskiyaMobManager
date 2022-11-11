package apple.voltskiya.mob_manager.storage;

import apple.voltskiya.mob_manager.listen.MMSpawnListener;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MMSpawnedSaved {

    private final UUID uuid;
    private final List<String> listeners;

    public MMSpawnedSaved(UUID uuid, List<String> listeners) {
        this.uuid = uuid;
        this.listeners = listeners;
    }

    public boolean isDead() {
        @Nullable Entity me = Bukkit.getEntity(uuid);
        return me == null || me.isDead();
    }

    public void doLoad() {
        @Nullable Entity me = Bukkit.getEntity(uuid);
        if (me instanceof LivingEntity living) {
            MMSpawnListener.get().handle(living, listeners);
        }
    }

    public UUID getUUID() {
        return uuid;
    }
}
