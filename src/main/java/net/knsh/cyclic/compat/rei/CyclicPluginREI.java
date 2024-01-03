package net.knsh.cyclic.compat.rei;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.melter.RecipeMelter;
import net.knsh.cyclic.block.melter.ScreenMelter;
import net.knsh.cyclic.compat.rei.categories.MelterCategory;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Collections;
import java.util.Objects;

public class CyclicPluginREI implements REIClientPlugin {
    public static final CategoryIdentifier<CyclicDisplayREI<RecipeMelter>> MELTER = CategoryIdentifier.of(Cyclic.MOD_ID, "melter");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MelterCategory());
        registry.addWorkstations(MELTER, EntryStacks.of(CyclicBlocks.MELTER.block()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager rm = world.getRecipeManager();

        for (RecipeMelter recipe : rm.getAllRecipesFor(CyclicRecipeTypes.MELTER)) {
            CyclicDisplayREI<RecipeMelter> melterDisplay = new CyclicDisplayREI<>(recipe, MELTER,
                    EntryIngredients.ofIngredients(recipe.getIngredients()),
                    Collections.singletonList(EntryIngredients.of(convertToREIFluid(recipe.getRecipeFluid()))));
            registry.add(melterDisplay);
        }
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(75, 30, 20, 30), ScreenMelter.class, MELTER);
    }

    public static dev.architectury.fluid.FluidStack convertToREIFluid(FluidStack stack) {
        return dev.architectury.fluid.FluidStack.create(stack.getFluid(), stack.getAmount(), stack.getTag());
    }
}
