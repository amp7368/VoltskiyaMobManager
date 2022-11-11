package apple.voltskiya.mob_manager.listen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public interface ReSpawnListener extends HandleSpawnListenerParent {

    MMReSpawnResult doReSpawn(CreatureSpawnEvent event);

    default void registerReSpawnListener() {
        MMSpawnListener.get().addRespawnListener(this);
    }

    default MMReSpawnResult reSpawnResult(Entity entity, ServerLevel world,
        SpawnReason spawnReason) {
        return new MMReSpawnResult(entity, world, spawnReason);
    }

    default MMReSpawnResult reSpawnResult(Entity entity, ServerLevel world) {
        return new MMReSpawnResult(entity, world, SpawnReason.NATURAL);
    }
}
