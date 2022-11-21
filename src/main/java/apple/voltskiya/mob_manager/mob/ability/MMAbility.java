package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.activation.CreateActivationManager;

public abstract class MMAbility<Config extends CreateActivationManager> extends MMAbilityBase {

    protected final Config config;

    public MMAbility(MMSpawned mob, Config config) {
        super(mob, config.activation(mob));
        this.config = config;
    }
}
