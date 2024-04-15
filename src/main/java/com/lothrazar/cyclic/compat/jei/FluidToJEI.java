package com.lothrazar.cyclic.compat.jei;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class FluidToJEI {
    public static IJeiFluidIngredient toJei(FluidStack stack) {
        return new IJeiFluidIngredient() {
            @Override
            public Fluid getFluid() {
                return stack.getFluid();
            }

            @Override
            public long getAmount() {
                return stack.getAmount();
            }

            @Override
            public Optional<CompoundTag> getTag() {
                return Optional.ofNullable(stack.getTag());
            }
        };
    }
}
