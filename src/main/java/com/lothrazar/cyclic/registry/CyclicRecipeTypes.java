package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.melter.RecipeMelter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class CyclicRecipeTypes {
    public static RecipeType<RecipeMelter> MELTER = Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(Cyclic.MOD_ID, "melter"), new RecipeType<RecipeMelter>() {});
    public static RecipeMelter.SerializeMelter MELTER_S = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Cyclic.MOD_ID, "melter"), new RecipeMelter.SerializeMelter());

    public static void register() {}
}
