package net.knsh.flib.cap.item;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidHandlerItemStack;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class FluidHandlerLookupStack extends FluidHandlerItemStack {
    public static final String FLUID_NBT_KEY = FluidHandlerItemStack.FLUID_NBT_KEY;

    public FluidHandlerLookupStack(ContainerItemContext container, long capacity) {
        super(container, capacity);
    }

    @Override
    public boolean setFluid(FluidStack fluid, TransactionContext tx) {
        return super.setFluid(fluid, tx);
    }
}
