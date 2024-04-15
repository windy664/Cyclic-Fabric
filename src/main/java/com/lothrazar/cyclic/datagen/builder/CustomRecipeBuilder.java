package com.lothrazar.cyclic.datagen.builder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface CustomRecipeBuilder<T> {
    T getResult();

    ResourceLocation getDefaultRecipeId(T ingredient);

    void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId);

    default void save(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        this.save(finishedRecipeConsumer, getDefaultRecipeId(this.getResult()));
    }

    default void save(Consumer<FinishedRecipe> finishedRecipeConsumer, String recipeId) {
        ResourceLocation resourceLocation = getDefaultRecipeId(this.getResult());
        ResourceLocation resourceLocation2 = new ResourceLocation(recipeId);
        if (resourceLocation2.equals(resourceLocation)) {
            throw new IllegalStateException("Recipe " + recipeId + " should remove its 'save' argument as it is equal to default one");
        } else {
            this.save(finishedRecipeConsumer, resourceLocation2);
        }
    }
}
