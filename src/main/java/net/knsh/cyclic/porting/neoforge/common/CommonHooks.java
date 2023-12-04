package net.knsh.cyclic.porting.neoforge.common;

import net.knsh.cyclic.porting.neoforge.events.LivingChangeTargetEvent;
import net.knsh.cyclic.porting.neoforge.events.LivingDamageEvent;
import net.knsh.cyclic.porting.neoforge.events.LivingDeathEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class CommonHooks {
    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        event = LivingChangeTargetEvent.EVENT.invoker().onLivingChangeTarget(event);
        return event;
    }

    public static LivingDamageEvent onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        event = LivingDamageEvent.EVENT.invoker().onLivingDamage(event);
        return event;
    }

    public static Boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        LivingDeathEvent event = new LivingDeathEvent(entity, src);
        return LivingDeathEvent.EVENT.invoker().onLivingDeath(event).isCanceled();
    }
}
