package apple.voltskiya.mob_manager.mob.ability.activation;

import apple.utilities.util.NumberUtils;
import apple.voltskiya.mob_manager.MobManager;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class ActivationRange extends Activation {

    public double minRange = 0;
    public double maxRange = -1;

    public ActivationRange() {
    }

    public ActivationRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public ActivationRange(double minRange, double maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public double bounds() {
        return Math.max(0, this.maxRange - this.minRange);
    }

    @Override
    public ActivationRange setMob(MMSpawned mob) {
        super.setMob(mob);
        if (!mob.isMob())
            notMobError(mob);
        AttributeInstance attribute = mob.getMob().getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (attribute != null && attribute.getBaseValue() < this.maxRange)
            attribute.setBaseValue(this.maxRange);
        return this;
    }

    private void notMobError(MMSpawned mob) {
        MobManager.get().logger().error(
            String.format("'%s:%s' is not a Mob, but ActivationRange requires mobs", mob.getEntity().getName(),
                mob.getEntity().getType().name()));
    }

    @Override
    public boolean canStartAbility() {
        LivingEntity target = getTarget();
        if (target == null)
            return false;
        double distance = getLocation().distance(target.getLocation());
        if (this.maxRange == -1)
            return distance >= this.minRange;
        return NumberUtils.betweenInclusiveDouble(this.minRange, distance, this.maxRange);
    }

    @Override
    public Activation copy() {
        return new ActivationRange(this.minRange, this.maxRange);
    }
}
