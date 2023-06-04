package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

public class MMAbilityManager implements HasMMSpawnedUtility {

    private final MMSpawned mob;
    private final List<MMAbilityBase> abilities = new ArrayList<>();
    @NotNull
    private MMAbilityManagerTask nextTick = MMAbilityManagerTask.DONE;

    public MMAbilityManager(MMSpawned mob) {
        this.mob = mob;
    }

    public void registerAbility(MMAbilityBase ability) {
        this.abilities.add(ability);
        int otherNextTick = ability.getNextTick();
        if (this.nextTick.isDone()) {
            schedule(otherNextTick);
            return;
        }
        if (otherNextTick < this.nextTick.scheduledFor) {
            this.nextTick.cancel();
            schedule(otherNextTick);
        }
    }

    private void schedule(int nextTick) {
        if (nextTick < Bukkit.getCurrentTick()) {
            MobManager.get().logger()
                .error(this.getMob().getName() + " set nextTick to " + nextTick + " which is before " + Bukkit.getCurrentTick());
            return;
        }
        int delay = nextTick - Bukkit.getCurrentTick();
        int taskId = MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick, delay);
        this.nextTick = new MMAbilityManagerTask(taskId, nextTick);
    }

    private void tick() {
        this.nextTick.setDone();
        int currentTick = Bukkit.getCurrentTick();
        this.abilities.sort(Comparator.comparingInt(MMAbilityBase::getNextTick));
        for (MMAbilityBase ability : this.abilities) {
            if (ability.getNextTick() <= currentTick) {
                ability.tick_();
            } else
                break;
        }
        if (!this.abilities.isEmpty())
            this.schedule(this.abilities.get(0).getNextTick());
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    public void onDeath(EntityDeathEvent event) {
        for (MMAbilityBase ability : this.abilities) {
            ability.onDeath(event);
        }
    }

    // runs on death as well
    public void disable() {
        this.nextTick.cancel();
        for (MMAbilityBase ability : this.abilities) {
            ability.disable();
        }
    }

    public void onDamage(EntityDamageEvent event) {
        for (MMAbilityBase ability : this.abilities) {
            ability.onDamage(event);
        }
    }

    public void onTarget(EntityTargetEvent event) {
        for (MMAbilityBase ability : this.abilities) {
            ability.onTarget(event);
        }
    }
}
