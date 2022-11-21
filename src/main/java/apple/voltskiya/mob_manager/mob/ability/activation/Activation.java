package apple.voltskiya.mob_manager.mob.ability.activation;

import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityConfigSection;
import org.bukkit.Bukkit;

public abstract class Activation implements HasMMSpawnedUtility, MMAbilityConfigSection {

    private transient int nextTick = 0;
    protected transient MMSpawned mob;

    @Override
    public MMSpawned getMMSpawned() {
        return mob;
    }

    public Activation setMob(MMSpawned mob) {
        this.mob = mob;
        return this;
    }

    /**
     * assume {@link #getNextTick()} is upheld
     *
     * @return if the ability can be started
     */
    public abstract boolean canStartAbility();

    public abstract Activation copy();

    // hooks
    public void onStartAbility() {
    }

    public void onContinueAbility() {
    }

    public void onFinishAbility() {
    }

    protected void setNextTick(int later) {
        this.nextTick = Bukkit.getCurrentTick() + later;
    }

    public int getNextTick() {
        return this.nextTick;
    }
}
