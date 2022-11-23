package apple.voltskiya.mob_manager.mob.ability.activation;

import apple.voltskiya.mob_manager.mob.MMSpawned;
import apple.voltskiya.mob_manager.mob.ability.MMAbilityBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import voltskiya.apple.utilities.chance.ChanceRolling;

public class ActivationManager {

    private final List<Activation> requirements;
    private final ChanceRolling chance;
    private final MMSpawned mob;
    private final int tickInterval;

    public ActivationManager(MMSpawned mob, int tickInterval, double activationChance) {
        this.mob = mob;
        this.tickInterval = tickInterval;
        this.requirements = new ArrayList<>(1);
        this.chance = new ChanceRolling(activationChance, tickInterval);
    }

    public ActivationManager add(Collection<Activation> add) {
        for (Activation activation : add)
            this.requirements.add(activation.copy().setMob(mob));
        return this;
    }

    public boolean canStartAbility() {
        return this.chance.roll() && requirements.stream().allMatch(Activation::canStartAbility);
    }

    public int getNextTickInterval() {
        int max = findNextTick();
        int nextTick = Bukkit.getCurrentTick() + this.tickInterval;
        return Math.max(max, nextTick);
    }

    public boolean isNextTickLater() {
        return Bukkit.getCurrentTick() != findNextTick();
    }

    private int findNextTick() {
        Optional<Integer> max = requirements.stream().map(Activation::getNextTick).max(Integer::compareTo);
        int nextTick = Bukkit.getCurrentTick();
        if (max.isEmpty())
            return nextTick;
        return Math.max(max.get(), nextTick);
    }

    public void onStartAbility() {
        requirements.forEach(Activation::onStartAbility);
    }

    public void onContinueAbility() {
        requirements.forEach(Activation::onContinueAbility);
    }

    public void onFinishAbility() {
        requirements.forEach(Activation::onFinishAbility);
    }

    public ActivationManager setAbility(MMAbilityBase abilityBase) {

        return this;
    }
}
