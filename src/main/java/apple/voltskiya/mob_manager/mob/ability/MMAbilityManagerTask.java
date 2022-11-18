package apple.voltskiya.mob_manager.mob.ability;

import org.bukkit.Bukkit;

public class MMAbilityManagerTask {

    public int taskId;
    public int scheduledFor;

    public MMAbilityManagerTask(int taskId, int scheduledFor) {
        this.taskId = taskId;
        this.scheduledFor = scheduledFor;

    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
