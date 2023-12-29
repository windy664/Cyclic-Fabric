package net.knsh.cyclic.library.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

public class RecipeUtil {
    public static NonNullList<Ingredient> getIngredientsArray(JsonObject obj) {
        JsonArray array = GsonHelper.getAsJsonArray(obj, "ingredients");
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (int i = 0; i < array.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(array.get(i));
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }
        return nonnulllist;
    }

    public static FluidStack getFluid(JsonObject fluidJson) {
        if (fluidJson.has("fluidTag")) {
            //      String fluidTag = fluidJson.get("fluidTag").getAsString();
        }
        String fluidId = GsonHelper.getAsString(fluidJson, "fluid");
        ResourceLocation resourceLocation = new ResourceLocation(fluidId);
        Fluid fluid = BuiltInRegistries.FLUID.get(resourceLocation);
        int count = fluidJson.get("count").getAsInt();
        if (count < 1) {
            count = 1;
        }
        return new FluidStack(fluid, count);
    }
}
