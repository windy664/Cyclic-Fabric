package net.knsh.cyclic.porting.neoforge.events;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.cyclic.porting.neoforge.bus.api.ForgeEvent;
import net.knsh.cyclic.porting.neoforge.bus.api.ICancellableEvent;
import net.knsh.cyclic.porting.neoforge.bus.fabric.ForgeEventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LivingDamageEvent extends ForgeEvent implements ICancellableEvent {
    public static final Event<LivingDamage> EVENT = ForgeEventFactory.create(LivingDamage.class, (listeners) -> (event) -> {
        for (LivingDamage listener : listeners) {
            listener.onLivingDamage(event);
        }
        return event;
    });

    @SuppressWarnings("unused")
    public static void doEventRegister(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> {
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

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
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
