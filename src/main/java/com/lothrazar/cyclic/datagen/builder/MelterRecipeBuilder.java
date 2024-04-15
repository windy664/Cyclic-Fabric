package com.lothrazar.cyclic.datagen.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.library.ingredient.EnergyIngredient;
import com.lothrazar.cyclic.registry.CyclicRecipeTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MelterRecipeBuilder implements CustomRecipeBuilder<FluidStack> {
    private final FluidStack fluidResult;
    private final EnergyIngredient energyRequired;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private String name = "";

    private MelterRecipeBuilder(FluidStack out, EnergyIngredient energyRequired) {
        this.fluidResult = out;
        this.energyRequired = energyRequired;
    }

    public static MelterRecipeBuilder melting(FluidStack out, EnergyIngredient energyRequired) {
        return new MelterRecipeBuilder(out, energyRequired);
    }

    public static MelterRecipeBuilder melting(Fluid fluid, long amount, int rf, int ticks) {
        return new MelterRecipeBuilder(new FluidStack(fluid, amount), new EnergyIngredient(rf, ticks));
    }

    public MelterRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public MelterRecipeBuilder requires(ItemLike item) {
        return this.requires(Ingredient.of(item));
    }

    public MelterRecipeBuilder requires(Ingredient ingredient) {
        if (this.ingredients.size() > 2) {
            ModCyclic.LOGGER.error("placeholder");
            return this;
        }
        this.ingredients.add(ingredient);
        return this;
    }

    public MelterRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public FluidStack getResult() {
        return this.fluidResult;
    }

    @Override
    public ResourceLocation getDefaultRecipeId(FluidStack ingredient) {
        ResourceLocation recipeId = BuiltInRegistries.FLUID.getKey(ingredient.getFluid());
        return new ResourceLocation(recipeId.getNamespace(), "melter/" + recipeId.getPath());
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
        if (!name.isBlank()) {
            recipeId = new ResourceLocation(recipeId.getNamespace(), "melter/" + name);
        }

        finishedRecipeConsumer.accept(
                new MelterRecipeBuilder.Result(
                        recipeId,
                        fluidResult,
                        energyRequired,
                        ingredients
                )
        );
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final FluidStack result;
        private final EnergyIngredient energyRequired;
        private final List<Ingredient> ingredients;

        public Result(ResourceLocation id, FluidStack result, EnergyIngredient energyRequired, List<Ingredient> ingredients) {
            this.id = id;
            this.result = result;
            this.ingredients = ingredients;
            this.energyRequired = energyRequired;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray ingredientsArray = new JsonArray();

            for (Ingredient ingredient : this.ingredients) {
                ingredientsArray.add(ingredient.toJson());
            }

            json.add("ingredients", ingredientsArray);
            JsonObject energyObject = new JsonObject();
            energyObject.addProperty("rfpertick", energyRequired.getRfPertick());
            energyObject.addProperty("ticks", energyRequired.getTicks());
            json.add("energy", energyObject);
            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("fluid", BuiltInRegistries.FLUID.getKey(result.getFluid()).toString());
            resultObject.addProperty("count", result.getAmount());
            json.add("result", resultObject);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return CyclicRecipeTypes.MELTER_S;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
