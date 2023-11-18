package net.knsh.cyclic.datagen.recipes.minecraft;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.function.Consumer;

public class CyclicCrafting extends FabricRecipeProvider {
    public CyclicCrafting(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        shapedRecipe(CyclicBlocks.CONVEYOR.block(), 8, "r r", "rcr", "r r")
                .define('r', Items.RAIL).define('c', Items.REDSTONE)
                .unlockedBy(FabricRecipeProvider.getHasName(Items.REDSTONE), FabricRecipeProvider.has(Items.REDSTONE))
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
