package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.mob.MMSpawned;

public abstract class MMAbility<Config> extends MMAbilityBase {

    protected final Config config;

    public MMAbility(MMSpawned mob, Config config, MMAbilityActivation activation) {
        super(mob, activation);
        this.config = config;
    }
}
