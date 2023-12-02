package net.knsh.cyclic.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.knsh.cyclic.event.fabric.BeforeDamageCallback;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void hurt(DamageSource damageSource, float damageAmount, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef damage) {
        damage.set(BeforeDamageCallback.BEFORE_DAMAGE.invoker().beforeDamage(damageSource, damage.get()));
    }
}
