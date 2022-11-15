package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.listen.SpawnListener;

public abstract class MMAbilityConfig implements SpawnListener {

    public int tickInterval = 20;
    public double activationChance = 0.05;
    public int cooldown = 100;

    public MMAbilityActivation activation() {
        return new MMAbilityActivation(this.tickInterval, this.activationChance, this.cooldown);
    }
}
