package net.knsh.cyclic.porting.neoforge.common;

import net.knsh.cyclic.porting.neoforge.events.experimental.LivingChangeTargetEvent;
import net.knsh.cyclic.porting.neoforge.events.experimental.LivingDamageEvent;
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
}
