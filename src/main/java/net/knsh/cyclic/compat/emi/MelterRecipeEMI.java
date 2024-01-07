package net.knsh.cyclic.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.knsh.cyclic.block.melter.RecipeMelter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MelterRecipeEMI implements EmiRecipe {
    private static final int FONT = 10592673;
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final RecipeMelter recipe;

    public MelterRecipeEMI(RecipeMelter recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getIngredients().get(0)), EmiIngredient.of(recipe.getIngredients().get(1)));
        this.output = List.of(EmiStack.of(recipe.getRecipeFluid().getFluid(), recipe.getRecipeFluid().getAmount()));
        this.recipe = recipe;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CyclicPluginEMI.MELTER;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 169;
    }

    @Override
    public int getDisplayHeight() {
        return 69;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var font = Minecraft.getInstance().font;
        widgets.addText(Component.literal(recipe.getEnergy().getTicks() + " t"), 70, 40, FONT, false);
        widgets.addText(Component.literal(recipe.getEnergy().getRfPertick() + " RF/t"), 70, 50, FONT, false);
        widgets.addText(Component.literal(recipe.getEnergy().getEnergyTotal() + " RF"), 70, 60, FONT, false);

        widgets.addFillingArrow(75, 19, recipe.getEnergyCost());

        widgets.addSlot(input.get(0), 4, 19);
        widgets.addSlot(input.get(1), 22, 19);
        widgets.addTank(output.get(0), 140, 19, 18, 18, (int) recipe.getRecipeFluid().getAmount());
    }
}
