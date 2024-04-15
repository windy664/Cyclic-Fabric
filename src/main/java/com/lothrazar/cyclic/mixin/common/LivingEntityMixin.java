package com.lothrazar.cyclic.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lothrazar.cyclic.enchant.ReachEnchant;
import com.lothrazar.cyclic.event.fabric.BeforeDamageCallback;
import com.lothrazar.library.util.AttributesUtil;
import com.lothrazar.library.util.EnchantUtil;
import com.lothrazar.cyclic.registry.CyclicEnchants;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique private final LivingEntity entity = (LivingEntity) (Object) this;

    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void beforeActuallyHurt(LivingEntity instance, DamageSource damageSource, float damageAmount, @NotNull Operation<Void> original) {
        damageAmount = BeforeDamageCallback.BEFORE_DAMAGE.invoker().beforeDamage(damageSource, damageAmount);
        original.call(instance, damageSource, damageAmount);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;

        if (ReachEnchant.CFG.get()) {
            ItemStack armor = EnchantUtil.getFirstArmorStackWithEnchant(player, CyclicEnchants.REACH.get());
            int level = 0;
            if (!armor.isEmpty()) {
                level = EnchantmentHelper.getItemEnchantmentLevel(CyclicEnchants.REACH.get(), armor);
            }
            if (level > 0) {
                AttributesUtil.setPlayerReach(ReachEnchant.ENCHANTMENT_REACH_ID, player, ReachEnchant.REACH_BOOST.get());
            } else {
                AttributesUtil.removePlayerReach(ReachEnchant.ENCHANTMENT_REACH_ID, player);
            }
        }
    }
}
