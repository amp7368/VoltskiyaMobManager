package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Random;
import org.bukkit.Bukkit;

public abstract class MMAbilityBase implements HasMMSpawnedUtility {

    protected final MMSpawned mob;
    protected final MMAbilityActivation activation;
    protected final Random random = new Random();

    private int nextTick;

    private boolean isRunning = false;

    public MMAbilityBase(MMSpawned mob, MMAbilityActivation activation) {
        this.mob = mob;
        this.activation = activation;
        setNextTickLater(activation.getCooldown());
        this.mob.getAbilities().registerAbility(this);
    }


    protected final void tick_() {
        if (this.isRunning) {
            setNextTickLater(activation.getCooldown());
            return;
        }

        if (rollToTick() && this.canStartAbility())
            startAbility_();
        else
            setNextTickLater(activation.getTickInterval());
    }

    protected boolean canStartAbility() {
        return true;
    }

    private void startAbility_() {
        if (this.isAbilityBlocking()) {
            setBlocked(true);
        }
        setNextTickLater(activation.getCooldown());
        this.isRunning = true;
        startAbility();
    }

    protected abstract void startAbility();

    protected void finishAbility() {
        if (this.isAbilityBlocking()) {
            setBlocked(false);
        }
        this.isRunning = false;
        setNextTickLater(activation.getCooldown());
    }


    protected boolean isAbilityBlocking() {
        return true;
    }


    public int getNextTick() {
        return this.nextTick;
    }

    private void setNextTickLater(int delay) {
        this.nextTick = Bukkit.getCurrentTick() + delay;
    }

    private boolean rollToTick() {
        return this.random.nextDouble() <= activation.minRollToActivate();
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    public abstract void doDeath();
}