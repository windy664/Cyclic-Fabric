package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.enchant.*;
import com.lothrazar.cyclic.Cyclic;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class CyclicEnchants {
    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    public static Enchantment TRAVELLER = registerEnchant(TravellerEnchant.ID, new TravellerEnchant());
    public static Enchantment AUTOSMELT = registerEnchant(AutoSmeltEnchant.ID, new AutoSmeltEnchant());
    public static Enchantment BEEKEEPER = registerEnchant(BeekeeperEnchant.ID, new BeekeeperEnchant());
    public static Enchantment BEHEADING = registerEnchant(BeheadingEnchant.ID, new BeheadingEnchant());
    public static Enchantment REACH = registerEnchant(ReachEnchant.ID, new ReachEnchant());
    public static Enchantment DISARM = registerEnchant(DisarmEnchant.ID, new DisarmEnchant());
    public static Enchantment PEARL = registerEnchant(EnderPearlEnchant.ID, new EnderPearlEnchant());

    public static void register() {}

    private static Enchantment registerEnchant(String id, Enchantment enchant) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(Cyclic.MOD_ID, id), enchant);
    }
}
