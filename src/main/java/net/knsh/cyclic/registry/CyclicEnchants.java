package net.knsh.cyclic.registry;

import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.enchant.TravellerEnchant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class CyclicEnchants {
    public static Enchantment TRAVELLER = registerEnchant("traveler", new TravellerEnchant());

    public static void register() {}

    private static Enchantment registerEnchant(String id, Enchantment enchant) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(Cyclic.MOD_ID, id), enchant);
    }
}
