package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

public class MMAbilityManager implements HasMMSpawnedUtility {

    @Nullable
    private MMAbilityManagerTask nextTick = null;

    private final MMSpawned mob;
    private final List<MMAbilityBase> abilities = new ArrayList<>();

    public MMAbilityManager(MMSpawned mob) {
        this.mob = mob;
    }

    public void registerAbility(MMAbilityBase ability) {
        this.abilities.add(ability);
        int otherNextTick = ability.getNextTick();
        if (this.nextTick == null) {
            schedule(otherNextTick);
            return;
        }
        if (otherNextTick < this.nextTick.scheduledFor) {
            this.nextTick.cancel();
            schedule(otherNextTick);
        }
    }

    private void schedule(int otherNextTick) {
        int delay = otherNextTick - Bukkit.getCurrentTick();
        int taskId = MMVoltskiyaPlugin.get().scheduleSyncDelayedTask(this::tick, delay);
        this.nextTick = new MMAbilityManagerTask(taskId, otherNextTick);
    }

    private void tick() {
        int currentTick = Bukkit.getCurrentTick();
        this.abilities.sort(Comparator.comparingInt(MMAbilityBase::getNextTick));
        for (MMAbilityBase ability : this.abilities) {
            if (ability.getNextTick() <= currentTick) {
                ability.tick_();
            } else
                break;
        }
        if (this.abilities.isEmpty())
            this.nextTick = null;
        else
            this.schedule(this.abilities.get(0).getNextTick());
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    public void onDeath(EntityDeathEvent event) {
        if (this.nextTick != null)
            this.nextTick.cancel();
        for (MMAbilityBase ability : this.abilities) {
            ability.onDeath(event);
        }
    }

    // runs on death as well
    public void disable() {
        for (MMAbilityBase ability : this.abilities) {
            ability.disable();
        }
    }

    public void onDamage(EntityDamageEvent event) {
        for (MMAbilityBase ability : this.abilities) {
            ability.onDamage(event);
        }
    }
}
