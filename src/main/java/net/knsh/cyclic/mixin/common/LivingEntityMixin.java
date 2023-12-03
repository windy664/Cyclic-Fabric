package net.knsh.cyclic.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.knsh.cyclic.event.fabric.BeforeDamageCallback;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void beforeActuallyHurt(LivingEntity instance, DamageSource damageSource, float damageAmount, Operation<Void> original) {
        damageAmount = BeforeDamageCallback.BEFORE_DAMAGE.invoker().beforeDamage(damageSource, damageAmount);
        original.call(instance, damageSource, damageAmount);
    }
}
