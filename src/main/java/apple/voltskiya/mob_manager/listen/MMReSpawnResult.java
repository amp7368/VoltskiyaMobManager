package apple.voltskiya.mob_manager.listen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public record MMReSpawnResult(Entity entity, ServerLevel world,
                              CreatureSpawnEvent.SpawnReason spawnReason) {

    public void addEntityToWorld() {
        world.addFreshEntity(entity, spawnReason);
    }
}
