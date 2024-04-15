package com.lothrazar.cyclic.enchant;

import com.lothrazar.library.enchant.EnchantmentCyclic;
import com.lothrazar.library.util.EntityUtil;
import com.lothrazar.library.util.SoundUtil;
import dev.architectury.event.events.common.InteractionEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnderPearlEnchant extends EnchantmentCyclic {
    public static final int COOLDOWN = 6 * 20;
    private static final float VELOCITY = 1.5F; //Same as EnderPearlItem
    private static final float INNACCURACY = 1F; //Same as EnderPearlItem
    public static final String ID = "ender";
    public static ForgeConfigSpec.BooleanValue CFG;

    public EnderPearlEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!isEnabled()) {
                return InteractionResultHolder.pass(ItemStack.EMPTY);
            }
            if (!player.isSpectator()) {
                if (!world.isClientSide()) {
                    int level = EnchantmentHelper.getEnchantmentLevel(this, player);
                    if (level > 0) {
                        int adjustedCooldown = COOLDOWN / level;
                        if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                            return InteractionResultHolder.pass(ItemStack.EMPTY);
                        }
                        ThrownEnderpearl pearl = new ThrownEnderpearl(world, player);
                        Vec3 lookVector = player.getLookAngle();
                        pearl.shoot(lookVector.x(), lookVector.y(), lookVector.z(), VELOCITY, INNACCURACY);
                        EntityUtil.setCooldownItem(player, player.getMainHandItem().getItem(), adjustedCooldown);
                        SoundUtil.playSound(player, SoundEvents.ENDER_PEARL_THROW, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
                        world.addFreshEntity(pearl);
                        //block propogation of event
                        return InteractionResultHolder.success(player.getMainHandItem());
                    }
                }
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        });
    }

    @Override
    public boolean isEnabled() {
        return CFG.get();
    }

    @Override
    public boolean isTradeable() {
        return isEnabled() && super.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {
        return isEnabled() && super.isDiscoverable();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return isEnabled() && super.isAllowedOnBooks();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
