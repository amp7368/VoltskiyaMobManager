package apple.voltskiya.mob_manager.mob;

import apple.voltskiya.mob_manager.mob.ability.MMAbilityManager;
import apple.voltskiya.mob_manager.mob.ai.MMAiManager;
import apple.voltskiya.mob_manager.mob.event.MMEventManager;
import apple.voltskiya.mob_manager.storage.MMRuntimeDatabase;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class MMSpawned implements HasEntityUtility {


    private final Entity entity;
    private final MMEventManager events = new MMEventManager(this);
    private final MMAbilityManager abilities = new MMAbilityManager(this);
    private final MMAiManager ai = new MMAiManager(this);
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

    public MMAiManager getAi() {
        return this.ai;
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

    public void onDeath(EntityDeathEvent event) {
        this.abilities.onDeath(event);
        this.events.onDeath(event);
    }

    // runs on death as well
    public void disable() {
        this.abilities.disable();
        this.events.disable();
        this.ai.disable();
    }

    public void onDamage(EntityDamageEvent event) {
        this.abilities.onDamage(event);
        this.events.onDamage(event);

    }
}
