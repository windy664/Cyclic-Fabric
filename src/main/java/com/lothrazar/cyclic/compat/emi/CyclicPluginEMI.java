package com.lothrazar.cyclic.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.melter.RecipeMelter;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CyclicPluginEMI implements EmiPlugin {
    public static final Map<ResourceLocation, EmiRecipeCategory> ALL = new LinkedHashMap<>();

    public static final EmiRecipeCategory
            MELTER = register("melter", EmiStack.of(CyclicBlocks.MELTER.block()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MELTER);
        registry.addWorkstation(MELTER, EmiStack.of(CyclicBlocks.MELTER.block()));

        RecipeManager manager = registry.getRecipeManager();

        List<RecipeMelter> meltingRecipes = manager.getAllRecipesFor(CyclicRecipeTypes.MELTER);
        for (RecipeMelter recipe : meltingRecipes) {
            registry.addRecipe(new MelterRecipeEMI(recipe));
        }
    }

    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        ResourceLocation id = new ResourceLocation(Cyclic.MOD_ID, name);
        EmiRecipeCategory category = new EmiRecipeCategory(id, icon);
        ALL.put(id, category);
        return category;
    }
}
