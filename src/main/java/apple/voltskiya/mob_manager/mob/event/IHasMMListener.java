package apple.voltskiya.mob_manager.mob.event;

import apple.voltskiya.mob_manager.listen.order.MMSpawningOrder;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public interface IHasMMListener {

    default void onDeath(EntityDeathEvent event) {
        toEachListener(l -> l.onDeath(getMMSpawned(), event));
    }

    default void disable() {
        toEachListener(l -> l.disable(getMMSpawned()));
    }

    default void onDamage(EntityDamageEvent event) {
        toEachListener(l -> l.onDamage(getMMSpawned(), event));
    }

    default void onTarget(EntityTargetEvent event) {
        toEachListener(l -> l.onTarget(getMMSpawned(), event));
    }


    MMSpawned getMMSpawned();

    List<IMMListener> listeners();

    default void toEachListener(Consumer<IMMListener> fn) {
        listeners().forEach(fn);
    }

    default int priorityValue() {
        return priority().order();
    }

    default MMSpawningOrder priority() {
        return MMSpawningOrder.NORMAL;
    }

}
