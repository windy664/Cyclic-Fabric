package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.enchant.AutoSmeltEnchant;
import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class CyclicLootModifiers {
    public static final LazyRegistrar<Codec<? extends IGlobalLootModifier>> LOOT = LazyRegistrar.create(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS_KEY, ModCyclic.MODID);
    public static final RegistryObject<Codec<AutoSmeltEnchant.EnchantAutoSmeltModifier>> AUTO_SMELT = LOOT.register(AutoSmeltEnchant.ID, AutoSmeltEnchant.EnchantAutoSmeltModifier.CODEC);
}
