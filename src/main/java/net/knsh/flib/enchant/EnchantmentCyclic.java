package net.knsh.flib.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentCyclic extends Enchantment {
    protected EnchantmentCyclic(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    public int getCurrentLevelTool(ItemStack stack) {
        if (!stack.isEmpty() && EnchantmentHelper.getEnchantments(stack).containsKey(this) && stack.getItem() != Items.ENCHANTED_BOOK) {
            return EnchantmentHelper.getEnchantments(stack).get(this);
        }
        return -1;
    }

    public int getCurrentLevelSlot(LivingEntity player, EquipmentSlot type) {
        ItemStack armor = player.getItemBySlot(type);
        int level = 0;
        if (!armor.isEmpty()) {
            EnchantmentHelper.getEnchantments(armor);
            if (EnchantmentHelper.getEnchantments(armor).containsKey(this)) {
                level = EnchantmentHelper.getEnchantments(armor).get(this);
            }
        }
        return level;
    }

    protected int getCurrentArmorLevel(LivingEntity player) {
        EquipmentSlot[] armors = new EquipmentSlot[] {
                EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS
        };
        int level = 0;
        for (EquipmentSlot slot : armors) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty()) {
                EnchantmentHelper.getEnchantments(armor);
                if (EnchantmentHelper.getEnchantments(armor).containsKey(this)) {
                    int newlevel = EnchantmentHelper.getEnchantments(armor).get(this);
                    if (newlevel > level) {
                        level = newlevel;
                    }
                }
            }
        }
        return level;
    }

    protected int getLevelAll(LivingEntity p) {
        return Math.max(getCurrentArmorLevel(p), getCurrentLevelTool(p));
    }

    protected ItemStack getFirstArmorStackWithEnchant(LivingEntity player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        for (ItemStack main : player.getArmorSlots()) {
            if (!main.isEmpty() && EnchantmentHelper.getEnchantments(main).containsKey(this)) {
                return main;
            }
        }
        return ItemStack.EMPTY;
    }

    protected int getCurrentLevelTool(LivingEntity player) {
        if (player == null) {
            return -1;
        }
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return Math.max(getCurrentLevelTool(main), getCurrentLevelTool(off));
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isAllowedOnBooks() {
        return true;
    }
}
