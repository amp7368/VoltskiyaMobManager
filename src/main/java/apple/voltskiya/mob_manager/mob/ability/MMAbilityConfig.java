package apple.voltskiya.mob_manager.mob.ability;

import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationCooldown;
import apple.voltskiya.mob_manager.mob.ability.activation.ActivationManager;
import apple.voltskiya.mob_manager.mob.ability.activation.CreateActivationManager;
import com.voltskiya.lib.configs.data.instance.ConfigBase;
import java.util.Collection;
import java.util.Collections;

public abstract class MMAbilityConfig extends ConfigBase implements SpawnListener, CreateActivationManager {

    public ActivationCooldown cooldown = new ActivationCooldown(100);
    public int tickInterval = 20;
    public double activationChance = 0.05;


    @Override
    public ActivationManager createActivation(MMSpawned mob) {
        return new ActivationManager(mob, tickInterval, activationChance).add(Collections.singleton(cooldown));
    }
    public Collection<MMAbilityConfigSection> getSections() {
        return Collections.emptyList();
    }
}
