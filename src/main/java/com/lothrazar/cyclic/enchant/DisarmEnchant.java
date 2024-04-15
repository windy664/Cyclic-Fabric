package com.lothrazar.cyclic.enchant;

import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.config.ConfigRegistry;
import com.lothrazar.flib.enchant.EnchantmentCyclic;
import com.lothrazar.flib.util.StringParseUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DisarmEnchant extends EnchantmentCyclic {
    public static ForgeConfigSpec.IntValue PERCENTPERLEVEL;
    public static ForgeConfigSpec.BooleanValue CFG;
    public static final String ID = "disarm";

    public DisarmEnchant() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    public double getChanceToDisarm(int level) {
        float baseChance = PERCENTPERLEVEL.get() / 100F;
        return baseChance + (baseChance * (level - 1));
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
        if (!(target instanceof LivingEntity livingTarget)) {
            return;
        }
        if (!canDisarm(livingTarget)) {
            return;
        }
        List<ItemStack> toDisarm = new ArrayList<>();
        target.getHandSlots().forEach(itemStack -> {
            if (getChanceToDisarm(level) > user.level().random.nextDouble()) {
                toDisarm.add(itemStack);
            }
        });
        toDisarm.forEach(itemStack -> {
            boolean dropHeld = false;
            if (itemStack.equals(livingTarget.getMainHandItem())) {
                livingTarget.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                dropHeld = true;
            }
            else if (itemStack.equals(livingTarget.getOffhandItem())) {
                livingTarget.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                dropHeld = true;
            }
            if (dropHeld) {
                user.level().addFreshEntity(new ItemEntity(user.level(), livingTarget.getX(),
                        livingTarget.getY(), livingTarget.getZ(), itemStack));
            }
        });
        super.doPostAttack(user, target, level);
    }

    private boolean canDisarm(LivingEntity target) {
        String id = EntityType.getKey(target.getType()).toString();
        if (StringParseUtil.isInList(ConfigRegistry.getDisarmIgnoreList(), EntityType.getKey(target.getType()))) {
            Cyclic.LOGGER.info("disenchant ignored by: CONFIG LIST" + id);
            return false;
        }
        //default yes, its not in ignore list so canDisarm=true
        return true;
    }
}
