package net.knsh.cyclic.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.knsh.cyclic.registry.CyclicEnchants;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
    @WrapOperation(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean shouldHurt(Entity entity, DamageSource source, float amount, Operation<Boolean> original) {
        if (entity instanceof LivingEntity) {
            ItemStack armor = ((LivingEntity) entity).getItemBySlot(EquipmentSlot.LEGS);
            if (!armor.isEmpty()
                    && EnchantmentHelper.getEnchantments(armor).containsKey(CyclicEnchants.TRAVELLER)
                    && EnchantmentHelper.getEnchantments(armor).get(CyclicEnchants.TRAVELLER) > 0) {
                return original.call(entity, source, 0F);
            }
        }
        return original.call(entity, source, amount);
    }
}
