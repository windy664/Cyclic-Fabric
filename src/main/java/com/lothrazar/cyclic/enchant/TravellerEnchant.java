package com.lothrazar.cyclic.enchant;

import com.lothrazar.flib.enchant.EnchantmentCyclic;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;

public class TravellerEnchant extends EnchantmentCyclic {
    public static final String ID = "traveler";
    public static ForgeConfigSpec.BooleanValue CFG;

    public TravellerEnchant() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
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
    public boolean canEnchant(ItemStack stack) {
        boolean yes = isEnabled()
                && (stack.getItem() instanceof ArmorItem)
                && ((ArmorItem) stack.getItem()).getType() == ArmorItem.Type.LEGGINGS;
        return yes;
    }

    @Override
    public boolean isEnabled() {
        return CFG.get();
    }


}
