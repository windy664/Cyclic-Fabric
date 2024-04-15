package com.lothrazar.cyclic.enchant;

import com.lothrazar.flib.enchant.EnchantmentCyclic;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnderPearlEnchant extends EnchantmentCyclic {
    public static final int COOLDOWN = 6 * 20;
    private static final float VELOCITY = 1.5F; //Same as EnderPearlItem
    private static final float INNACCURACY = 1F; //Same as EnderPearlItem
    public static final String ID = "ender";
    public static ForgeConfigSpec.BooleanValue CFG;

    public EnderPearlEnchant() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    /*
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!isEnabled()) {
            return;
        }
        Level world = event.getLevel();
        if (!world.isClientSide) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(this, event.getItemStack());
            if (level > 0) {
                int adjustedCooldown = COOLDOWN / level;
                Player player = event.getEntity();
                if (player.getCooldowns().isOnCooldown(event.getItemStack().getItem())) {
                    return;
                }
                ThrownEnderpearl pearl = new ThrownEnderpearl(world, player);
                Vec3 lookVector = player.getLookAngle();
                pearl.shoot(lookVector.x(), lookVector.y(), lookVector.z(), VELOCITY, INNACCURACY);
                EntityUtil.setCooldownItem(player, event.getItemStack().getItem(), adjustedCooldown);
                SoundUtil.playSound(player, SoundEvents.ENDER_PEARL_THROW, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
                world.addFreshEntity(pearl);
                //block propogation of event
                //event.setResult(Result.DENY);
                event.setCanceled(true);
            }
        }
    }*/
}
