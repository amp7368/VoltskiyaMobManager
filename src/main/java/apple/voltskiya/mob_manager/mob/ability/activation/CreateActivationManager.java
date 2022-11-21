package apple.voltskiya.mob_manager.mob.ability.activation;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.Collection;
import java.util.Collections;

public interface CreateActivationManager {

    default ActivationManager activation(MMSpawned mob) {
        return this.createActivation(mob).add(this.getActivations());
    }

    ActivationManager createActivation(MMSpawned mob);

    default Collection<Activation> getActivations() {
        return Collections.emptyList();
    }
}
