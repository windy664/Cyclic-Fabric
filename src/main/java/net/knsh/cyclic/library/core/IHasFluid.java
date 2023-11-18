package net.knsh.cyclic.library.core;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.knsh.cyclic.library.capabilities.FluidTankBase;

public interface IHasFluid {
    FluidTankBase getFluid();

    void setFluid(FluidVariant fluid);

    void setFluidAmount(long amount);
}
