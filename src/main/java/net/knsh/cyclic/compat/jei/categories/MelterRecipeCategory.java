package net.knsh.cyclic.compat.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.melter.RecipeMelter;
import net.knsh.cyclic.compat.jei.FluidToJEI;
import net.knsh.flib.util.ChatUtil;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MelterRecipeCategory implements IRecipeCategory<RecipeMelter> {
    private static final int FONT = 10592673;
    private static final ResourceLocation ID = new ResourceLocation(Cyclic.MOD_ID, "melter");
    public static final RecipeType<RecipeMelter> TYPE = new RecipeType<>(ID, RecipeMelter.class);
    private final IDrawable gui;
    private final IDrawable icon;

    public MelterRecipeCategory(IGuiHelper helper) {
        gui = helper.drawableBuilder(new ResourceLocation(Cyclic.MOD_ID, "textures/jei/melter_recipe.png"), 0, 0, 169, 69).setTextureSize(169, 69).build();
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CyclicBlocks.MELTER.block()));
    }

    @Override
    public Component getTitle() {
        return ChatUtil.ilang(CyclicBlocks.MELTER.block().getDescriptionId());
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public IDrawable getBackground() {
        return gui;
    }

    @Override
    public RecipeType<RecipeMelter> getRecipeType() {
        return TYPE;
    }

    @Override
    public void draw(RecipeMelter recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics ms, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        ms.drawString(font, recipe.getEnergy().getTicks() + " t", 70, 20, FONT);
        ms.drawString(font, recipe.getEnergy().getRfPertick() + " RF/t", 70, 30, FONT);
        ms.drawString(font, recipe.getEnergy().getEnergyTotal() + " RF", 70, 40, FONT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeMelter recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 19).addIngredients(recipe.at(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 19).addIngredients(recipe.at(1));
        List<IJeiFluidIngredient> matchingFluids = List.of(FluidToJEI.toJei(recipe.getRecipeFluid()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 140, 19).addIngredients(FabricTypes.FLUID_STACK, matchingFluids);
    }
}
