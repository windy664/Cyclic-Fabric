package com.lothrazar.cyclic.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Collections;
import java.util.List;

// Taken from https://github.com/Fabricators-of-Create/Create/blob/mc1.18/fabric/dev/src/main/java/com/simibubi/create/compat/rei/display/CreateDisplay.java
public class CyclicDisplayREI<R extends Recipe<?>> implements Display {
    protected final R recipe;
    private final CategoryIdentifier<CyclicDisplayREI<R>> uid;
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public CyclicDisplayREI(R recipe, CategoryIdentifier<CyclicDisplayREI<R>> id, List<EntryIngredient> input, List<EntryIngredient> output) {
        this.recipe = recipe;
        this.uid = id;
        this.input = input;
        this.output = output;
    }

    public CyclicDisplayREI(R recipe, CategoryIdentifier<CyclicDisplayREI<R>> id) {
        this.uid = id;
        this.recipe = recipe;
        this.input = EntryIngredients.ofIngredients(recipe.getIngredients());
        this.output = Collections.singletonList(EntryIngredients.of(recipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
    }

    public R getRecipe() {
        return recipe;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return uid;
    }
}
