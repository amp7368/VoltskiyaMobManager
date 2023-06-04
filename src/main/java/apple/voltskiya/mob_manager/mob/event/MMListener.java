package apple.voltskiya.mob_manager.mob.event;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public interface MMListener extends IMMListener {

    default void onDeath(MMSpawned spawned, EntityDeathEvent event) {
    }

    default void disable(MMSpawned spawned) {
    }

    default void onDamage(MMSpawned spawned, EntityDamageEvent event) {
    }

    default void onTarget(MMSpawned spawned, EntityTargetEvent event) {
    }
}
