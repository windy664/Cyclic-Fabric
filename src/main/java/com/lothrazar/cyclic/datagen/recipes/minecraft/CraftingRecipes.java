package com.lothrazar.cyclic.datagen.recipes.minecraft;

import com.lothrazar.cyclic.registry.CyclicBlocks;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class CraftingRecipes extends FabricRecipeProvider {
    public CraftingRecipes(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        shapedRecipe(CyclicBlocks.CONVEYOR.block(), 8, "r r", "rcr", "r r")
                .define('r', Items.RAIL).define('c', Items.REDSTONE)
                .unlockedBy(FabricRecipeProvider.getHasName(Items.REDSTONE), FabricRecipeProvider.has(Items.REDSTONE))
                .save(exporter);

        shapedRecipe(CyclicBlocks.MELTER.block(), 1, "rqr", "gfg", "ooo")
                .define('r', Items.REDSTONE_BLOCK)
                .define('q', Items.QUARTZ)
                .define('g', Tags.Items.GLASS)
                .define('f', Items.FURNACE)
                .define('o', Items.OBSIDIAN)
                .unlockedBy(FabricRecipeProvider.getHasName(Items.QUARTZ), FabricRecipeProvider.has(Items.QUARTZ))
                .save(exporter);
    }

    // Util for cleaner recipe creation
    private static ShapedRecipeBuilder shapedRecipe(ItemLike item, int count, String topRow, String middleRow, String bottomRow) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, count).pattern(topRow).pattern(middleRow).pattern(bottomRow);
    }

    private static ShapedRecipeBuilder shapedRecipe(ItemLike item, int count, String topRow, String bottomRow) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, count).pattern(topRow).pattern(bottomRow);
    }

    private static ShapedRecipeBuilder shapedRecipe(ItemLike item, int count, String row) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, count).pattern(row);
    }
}
