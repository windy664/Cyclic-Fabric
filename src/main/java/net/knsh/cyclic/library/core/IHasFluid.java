package net.knsh.cyclic.library.core;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.knsh.cyclic.library.capabilities.FluidTankBase;

public interface IHasFluid {
    FluidStack getFluid();

    void setFluid(FluidStack fluid);
}
