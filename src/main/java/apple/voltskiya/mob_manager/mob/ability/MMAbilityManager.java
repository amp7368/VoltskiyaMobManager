package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.MMVoltskiyaPlugin;
import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
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
        boolean isBlocked = this.mob.isBlocked();
        int currentTick = Bukkit.getCurrentTick();
        for (MMAbilityBase ability : this.abilities) {
            if (ability.getNextTick() == currentTick) {
                if (!isBlocked || !ability.isAbilityBlocking())
                    ability.tick_();
            } else
                break;
        }
        this.abilities.sort(Comparator.comparingInt(MMAbilityBase::getNextTick));
        if (this.abilities.isEmpty())
            this.nextTick = null;
        else
            this.schedule(this.abilities.get(0).getNextTick());
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    public void doDeath() {
        if (this.nextTick != null)
            this.nextTick.cancel();
        for (MMAbilityBase ability : this.abilities) {
            ability.doDeath();
        }
    }
}
