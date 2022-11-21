package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationManager;
import java.util.Random;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class MMAbilityBase implements HasMMSpawnedUtility {

    protected final MMSpawned mob;
    protected final ActivationManager activation;
    protected final Random random = new Random();

    private int nextTick;

    private boolean isAbilityRunning = false;

    public MMAbilityBase(MMSpawned mob, ActivationManager activation) {
        this.mob = mob;
        this.activation = activation.setAbility(this);
        setNextTickLater();
        this.mob.getAbilities().registerAbility(this);
    }


    // while ticking, use activation#getTickInterval(), but while determining when to run, use activation#getNextCheck
    protected final void tick_() {
        if (this.isAbilityBlocking() && this.isBlocked()) {
            setNextTickLater();
            return;
        }
        if (this.isAbilityRunning) {
            activation.onContinueAbility();
            setNextTickLater();
            return;
        }

        if (this.canStartAbility_())
            startAbility_();
        else
            setNextTickLater();
    }

    private boolean canStartAbility_() {
        return this.activation.canStartAbility() && this.canStartAbility();
    }

    protected boolean canStartAbility() {
        return true;
    }

    private void startAbility_() {
        setRunning(true);
        this.activation.onStartAbility();
        this.startAbility();
    }

    protected abstract void startAbility();

    protected void finishAbility() {
        setRunning(false);
        this.activation.onFinishAbility();
        this.onFinishAbility();
    }

    private void setRunning(boolean isRunning) {
        if (this.isAbilityBlocking())
            setBlocked(isRunning);
        this.isAbilityRunning = isRunning;
        setNextTickLater();
    }


    protected boolean isAbilityBlocking() {
        return true;
    }

    public int getNextTick() {
        return this.nextTick;
    }

    private void setNextTickLater() {
        this.nextTick = activation.getNextTick();
    }

    @Override
    public MMSpawned getMMSpawned() {
        return this.mob;
    }

    // runs on death as well
    public void disable() {
        this.finishAbility();
        this.cleanUp(isDead());
    }

    public abstract void cleanUp(boolean isDead);

    public void onDeath(EntityDeathEvent event) {
    }

    protected void onFinishAbility() {
    }
}
