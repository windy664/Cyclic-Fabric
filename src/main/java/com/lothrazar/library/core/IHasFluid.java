package com.lothrazar.library.core;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public interface IHasFluid {
    FluidStack getFluid();

    void setFluid(FluidStack fluid);
}
