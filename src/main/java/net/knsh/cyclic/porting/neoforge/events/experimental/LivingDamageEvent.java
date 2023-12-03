package net.knsh.cyclic.porting.neoforge.events.experimental;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LivingDamageEvent {
    public static final Event<LivingDamage> EVENT = EventFactory.createArrayBacked(LivingDamage.class, (listeners) -> (event) -> {
        for (LivingDamage listener : listeners) {
            listener.onLivingDamage(event);
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
    public interface LivingDamage {
        LivingDamageEvent onLivingDamage(LivingDamageEvent event);
    }

    private final DamageSource source;
    private float amount;
    private LivingEntity entity;
    private boolean canceled = false;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    public void setCanceled(Boolean bool) {
        this.canceled = bool;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
