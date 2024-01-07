package net.knsh.flib.core;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;

public interface IHasFluid {
    FluidStack getFluid();

    void setFluid(FluidStack fluid);
}
