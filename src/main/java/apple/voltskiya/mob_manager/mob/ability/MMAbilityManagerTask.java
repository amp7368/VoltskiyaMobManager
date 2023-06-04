package apple.voltskiya.mob_manager.mob.ability;

import org.bukkit.Bukkit;

public class MMAbilityManagerTask {

    public static final MMAbilityManagerTask DONE = new MMAbilityManagerTask(0, 0).setDone();
    public int taskId;
    public int scheduledFor;
    private boolean isDone;

    public MMAbilityManagerTask(int taskId, int scheduledFor) {
        this.taskId = taskId;
        this.scheduledFor = scheduledFor;

    }

    public void cancel() {
        if (!this.isDone)
            Bukkit.getScheduler().cancelTask(this.taskId);
        this.setDone();
    }

    public boolean isDone() {
        return isDone;
    }

    public MMAbilityManagerTask setDone() {
        this.isDone = true;
        return this;
    }
}
