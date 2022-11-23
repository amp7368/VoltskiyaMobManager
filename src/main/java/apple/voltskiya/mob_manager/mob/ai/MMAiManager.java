package apple.voltskiya.mob_manager.mob.ai;

import apple.voltskiya.mob_manager.mob.HasMMSpawnedUtility;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;

public class MMAiManager implements HasMMSpawnedUtility {

    private final MMSpawned mob;
    private final List<Goal> goalSelectorGoals = new ArrayList<>(1);
    private final List<Goal> targetSelectorGoals = new ArrayList<>(1);

    public MMAiManager(MMSpawned mob) {
        this.mob = mob;
    }

    @Override
    public MMSpawned getMMSpawned() {
        return mob;
    }

    public void addGoalSelectorGoal(int i, Goal goal) {
        this.goalSelectorGoals.add(goal);
        mob.getNmsMob().goalSelector.addGoal(i, goal);
    }

    public void addTargetSelectorGoal(int i, Goal goal) {
        this.targetSelectorGoals.add(goal);
        mob.getNmsMob().targetSelector.addGoal(i, goal);
    }

    public void disable() {
        for (Goal goal : this.goalSelectorGoals)
            this.getNmsMob().goalSelector.removeGoal(goal);
        for (Goal goal : this.targetSelectorGoals)
            this.getNmsMob().targetSelector.removeGoal(goal);
    }
}
