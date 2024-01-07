package net.knsh.cyclic.enchant;

import net.knsh.flib.enchant.EnchantmentCyclic;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec;

public class AutoSmeltEnchant extends EnchantmentCyclic {
    public static final String ID = "auto_smelt";
    public static ForgeConfigSpec.BooleanValue CFG;

    public AutoSmeltEnchant() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    public boolean isEnabled() {
        return CFG.get();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return ench != Enchantments.SILK_TOUCH && ench != Enchantments.BLOCK_FORTUNE && super.checkCompatibility(ench);
    }
}
