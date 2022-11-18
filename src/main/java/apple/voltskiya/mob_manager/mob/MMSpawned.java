package apple.voltskiya.mob_manager.mob;

import apple.voltskiya.mob_manager.mob.ability.MMAbilityManager;
import apple.voltskiya.mob_manager.mob.event.MMEventManager;
import apple.voltskiya.mob_manager.storage.MMRuntimeDatabase;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class MMSpawned implements HasEntityUtility {


    private final Entity entity;
    private final MMEventManager events = new MMEventManager(this);
    private final MMAbilityManager abilities = new MMAbilityManager(this);
    private boolean isBlocked = false;

    public MMSpawned(@NotNull Entity entity) {
        this.entity = entity;
        MMRuntimeDatabase.addMob(this);
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }


    public MMEventManager getEvents() {
        return this.events;
    }

    public MMAbilityManager getAbilities() {
        return this.abilities;
    }

    public void doHandle() {
        this.events.doHandle();
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public boolean isBlocked() {
        return this.isBlocked;
    }

    public void doDeath(EntityDeathEvent event) {
        this.abilities.doDeath(event);
        this.events.doDeath(event);
    }

    // runs on death as well
    public void disable() {
        this.abilities.disable();
        this.events.disable();
    }
}
