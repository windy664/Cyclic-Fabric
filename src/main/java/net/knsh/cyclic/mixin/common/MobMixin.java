package net.knsh.cyclic.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.knsh.cyclic.porting.neoforge.common.CommonHooks;
import net.knsh.cyclic.porting.neoforge.events.experimental.LivingChangeTargetEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
    @WrapOperation(
            method = "setTarget",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;")
    )
    private LivingEntity onSetTarget(Mob instance, LivingEntity value, Operation<LivingEntity> original) {
        LivingChangeTargetEvent changeTargetEvent = CommonHooks.onLivingChangeTarget(instance, value, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if (!changeTargetEvent.isCanceled()) {
            return changeTargetEvent.getNewTarget();
        }
        return null;
    }
}
