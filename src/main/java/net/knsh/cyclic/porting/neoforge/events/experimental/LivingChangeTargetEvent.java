package net.knsh.cyclic.porting.neoforge.events.experimental;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.knsh.cyclic.porting.neoforge.events.ForgeEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LivingChangeTargetEvent implements ForgeEvent {
    public static final Event<LivingChangeTarget> EVENT = EventFactory.createArrayBacked(LivingChangeTarget.class, (listeners) -> (event) -> {
        for (LivingChangeTarget listener : listeners) {
            listener.onLivingChangeTarget(event);
        }
        return event;
    });

    public static void doEventRegister(Method method, Object object) {
        EVENT.register((event) -> {
            try {
                method.invoke(object, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return event;
        });
    }

    @FunctionalInterface
    public interface LivingChangeTarget {
        LivingChangeTargetEvent onLivingChangeTarget(LivingChangeTargetEvent event);
    }

    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    private LivingEntity newTarget;
    private Entity entity;
    private boolean canceled = false;

    public LivingChangeTargetEvent(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType) {
        this.entity = entity;
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    public void setCanceled(Boolean bool) {
        this.canceled = bool;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * {@return the new target of this entity.}
     */
    public LivingEntity getNewTarget() {
        return newTarget;
    }

    /**
     * Sets the new target this entity shall have.
     *
     * @param newTarget The new target of this entity.
     */
    public void setNewTarget(LivingEntity newTarget) {
        this.newTarget = newTarget;
    }

    /**
     * {@return the living target type.}
     */
    public ILivingTargetType getTargetType() {
        return targetType;
    }

    /**
     * {@return the original entity MC intended to use as a target before firing this event.}
     */
    public LivingEntity getOriginalTarget() {
        return originalTarget;
    }

    /**
     * A living target type indicates what kind of system caused a change of
     * targets. For a list of default target types, take a look at
     * {@link LivingTargetType}.
     */
    public static interface ILivingTargetType {

    }

    /**
     * This enum contains two default living target types.
     */
    public static enum LivingTargetType implements ILivingTargetType {
        /**
         * This target type indicates that the target has been set by calling {@link net.minecraft.world.entity.Mob#setTarget(LivingEntity)}.
         */
        MOB_TARGET,
        /**
         * This target type indicates that the target has been set by the {@link net.minecraft.world.entity.ai.behavior.StartAttacking} behavior.
         */
        BEHAVIOR_TARGET;
    }
}
