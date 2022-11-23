package apple.voltskiya.mob_manager.listen;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.storage.MMRuntimeDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class MMBukkitEntityListener implements Listener {

    public MMBukkitEntityListener() {
        MMVoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        @Nullable MMSpawned mob = MMRuntimeDatabase.getMob(event.getEntity().getUniqueId());
        if (mob != null) {
            mob.onDamage(event);
        }
    }

}
