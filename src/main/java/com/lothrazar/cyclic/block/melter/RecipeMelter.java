package com.lothrazar.cyclic.block.melter;

import com.google.gson.JsonObject;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.registry.CyclicRecipeTypes;
import com.lothrazar.flib.ingredient.EnergyIngredient;
import com.lothrazar.flib.util.RecipeUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class RecipeMelter implements Recipe<TileMelter> {
    private final ResourceLocation id;
    private NonNullList<Ingredient> ingredients = NonNullList.create();
    private final FluidStack outFluid;
    private final EnergyIngredient energy;

    public RecipeMelter(ResourceLocation id, NonNullList<Ingredient> ingredientsIn, FluidStack out, EnergyIngredient energy) {
        this.id = id;
        this.energy = energy;
        ingredients = ingredientsIn;
        if (ingredients.size() == 1) {
            ingredients.add(Ingredient.EMPTY);
        }
        if (ingredients.size() != 2) {
            throw new IllegalArgumentException("Melter recipe must have at most two ingredients");
        }
        this.outFluid = out;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(TileMelter inv, Level worldIn) {
        try {
            TileMelter tile = inv;
            //if first one matches check second
            //if first does not match, fail
            boolean matchLeft = matches(tile.getStackInputSlot(0), ingredients.get(0));
            boolean matchRight = matches(tile.getStackInputSlot(1), ingredients.get(1));
            return matchLeft && matchRight;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public static class Type implements RecipeType<RecipeMelter> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "melter";
    }

    public boolean matches(ItemStack current, Ingredient ing) {
        if (ing == Ingredient.EMPTY) {
            //it must be empty
            return current.isEmpty();
        }
        if (current.isEmpty()) {
            return ing == Ingredient.EMPTY;
        }
        return ing.test(current);
    }

    public ItemStack[] ingredientAt(int slot) {
        Ingredient ing = at(slot);
        return ing.getItems();
    }

    public Ingredient at(int slot) {
        return ingredients.get(slot);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess ra) {
        return ItemStack.EMPTY;
    }

    public FluidStack getRecipeFluid() {
        return outFluid.copy();
    }

    @Override
    public RecipeType<?> getType() {
        return CyclicRecipeTypes.MELTER;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CyclicRecipeTypes.MELTER_S;
    }

    @Override
    public ItemStack assemble(TileMelter t, RegistryAccess ra) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width <= 2 && height <= 1;
    }

    public static class SerializeMelter implements RecipeSerializer<RecipeMelter> {

        public SerializeMelter() {}

        /**
         * The fluid stuff i was helped out a ton by looking at this https://github.com/mekanism/Mekanism/blob/921d10be54f97518c1f0cb5a6fc64bf47d5e6773/src/api/java/mekanism/api/SerializerHelper.java#L129
         */
        @Override
        public RecipeMelter fromJson(ResourceLocation recipeId, JsonObject json) {
            RecipeMelter r = null;
            try {
                NonNullList<Ingredient> list = RecipeUtil.getIngredientsArray(json);
                JsonObject result = json.get("result").getAsJsonObject();
                FluidStack fluid = RecipeUtil.getFluid(result);
                r = new RecipeMelter(recipeId, list, fluid, new EnergyIngredient(json));
            }
            catch (Exception e) {
                Cyclic.LOGGER.error("Error loading recipe " + recipeId, e);
            }
            return r;
        }

        @Override
        public RecipeMelter fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buf) {
            NonNullList<Ingredient> ins = NonNullList.create();
            // ing, ing, fluid, (int,int)
            ins.add(Ingredient.fromNetwork(buf));
            ins.add(Ingredient.fromNetwork(buf));
            return new RecipeMelter(recipeId, ins, FluidStack.readFromPacket(buf),
                    new EnergyIngredient(buf.readInt(), buf.readInt()));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RecipeMelter recipe) {
            //ing, ing, fluid, (int,int)
            Ingredient zero = recipe.ingredients.get(0);
            Ingredient one = recipe.ingredients.get(1);
            zero.toNetwork(buf);
            one.toNetwork(buf);
            recipe.outFluid.writeToPacket(buf);
            buf.writeInt(recipe.energy.getRfPertick());
            buf.writeInt(recipe.energy.getTicks());
        }
    }

    public int getEnergyCost() {
        return this.energy.getEnergyTotal();
    }

    public EnergyIngredient getEnergy() {
        return energy;
    }
}
