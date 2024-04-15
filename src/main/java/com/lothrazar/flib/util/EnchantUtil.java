package com.lothrazar.flib.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantUtil {
    public static int getCurrentLevelSlot(LivingEntity player, EquipmentSlot type, Enchantment enchant) {
        ItemStack armor = player.getItemBySlot(type);
        int level = 0;
        if (!armor.isEmpty()) {
            EnchantmentHelper.getEnchantments(armor);
            if (EnchantmentHelper.getEnchantments(armor).containsKey(enchant)) {
                level = EnchantmentHelper.getEnchantments(armor).get(enchant);
            }
        }
        return level;
    }

    public static ItemStack getFirstArmorStackWithEnchant(LivingEntity player, Enchantment enchant) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty()) {
                EnchantmentHelper.getEnchantments(armor);
                if (EnchantmentHelper.getEnchantments(armor).containsKey(enchant)) {
                    return armor;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
