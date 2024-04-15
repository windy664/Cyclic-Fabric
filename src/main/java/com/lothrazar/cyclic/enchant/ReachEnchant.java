package com.lothrazar.cyclic.enchant;

import com.lothrazar.library.enchant.EnchantmentCyclic;
import com.lothrazar.cyclic.registry.CyclicEnchants;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.UUID;

public class ReachEnchant extends EnchantmentCyclic {
    private static final String NBT_REACH_ON = "reachon";
    public static ForgeConfigSpec.IntValue REACH_BOOST;
    public static final String ID = "reach";
    public static ForgeConfigSpec.BooleanValue CFG;

    public static final UUID ENCHANTMENT_REACH_ID = UUID.fromString("1abcdef2-eff2-4a81-b92b-a1cb95f115c6");

    public ReachEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
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
        return 1;
    }
}
