package apple.voltskiya.mob_manager.mob.ability.activation;

public class ActivationCooldown extends Activation {

    public int cooldown = 20 * 10;


    public ActivationCooldown() {
    }

    public ActivationCooldown(int cooldown) {
        this.cooldown = cooldown;
    }


    @Override
    public boolean canStartAbility() {
        return true;
    }


    @Override
    public void onStartAbility() {
        this.setNextTick(this.cooldown);
    }

    @Override
    public void onContinueAbility() {
        this.setNextTick(this.cooldown);
    }

    @Override
    public void onFinishAbility() {
        this.setNextTick(this.cooldown);
    }

    @Override
    public Activation copy() {
        return new ActivationCooldown(cooldown);
    }
}
