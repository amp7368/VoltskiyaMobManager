package apple.voltskiya.mob_manager.mob.event;

import apple.voltskiya.mob_manager.listen.order.MMSpawningOrder;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public interface IMMListener {

    void onDeath(MMSpawned spawned, EntityDeathEvent event);

    void disable(MMSpawned spawned);

    void onDamage(MMSpawned spawned, EntityDamageEvent event);

    void onTarget(MMSpawned spawned, EntityTargetEvent event);

    default int orderValue() {
        return order().order();
    }

    default MMSpawningOrder order() {
        return MMSpawningOrder.NORMAL;
    }
}
