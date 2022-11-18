package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.listen.respawn.MMReSpawnResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public interface ReSpawnListener extends HandleSpawnListenerParent {

    MMReSpawnResult doReSpawn(Entity entity);

    default void registerReSpawnListener() {
        MMEventDispatcher.get().addRespawnListener(this);
    }

    @Override
    default String getExtensionTag() {
        return "mob";
    }

    default MMReSpawnResult reSpawnResult(Entity entity, ServerLevel world,
        SpawnReason spawnReason) {
        return new MMReSpawnResult(entity, world, spawnReason);
    }

    default MMReSpawnResult reSpawnResult(Entity entity, ServerLevel world) {
        return new MMReSpawnResult(entity, world, SpawnReason.NATURAL);
    }
}
