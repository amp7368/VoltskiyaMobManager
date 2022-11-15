package apple.voltskiya.mob_manager.mob.ability;

public class MMAbilityActivation {

    private int tickInterval;

    private double activationChance;

    private int cooldown;
    private double minChance;


    public MMAbilityActivation(int tickInterval, double activationChance, int cooldown) {
        this.tickInterval = tickInterval;
        this.activationChance = activationChance;
        this.cooldown = cooldown;
        resetMinChance();
    }

    private void resetMinChance() {
        this.minChance = 1 - Math.pow(1 - this.activationChance, this.tickInterval);
    }

    public void setActivationChance(double activationChance) {
        this.activationChance = activationChance;
        this.resetMinChance();
    }

    public int getTickInterval() {
        return this.tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
        this.resetMinChance();
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        this.resetMinChance();
    }

    public double minRollToActivate() {
        return this.minChance;
    }

    public int getCooldown() {
        return cooldown;
    }
}
