package com.lothrazar.cyclic.util;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * This interface can be implemented onto {@link Storage<FluidVariant>} to give it forge-like methods based on the
 * IFluidHandler interface from Forge.
 * A OptionalSlottedFluidTank might/can have more than one slot but also works for single slot fluid storages.
 *
 * @author KnownSH
 * Docs taken from MinecraftForge
 */
public interface OptionalSlottedFluidTank extends Storage<FluidVariant> {
    /**
     * Returns the FluidStack in the FluidStorage.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents.
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @NotNull
    default FluidStack getFluidStack(int tank) {
        StoragePreconditions.notNegative(tank);
        Iterator<StorageView<FluidVariant>> tankIterator = iterator();
        int slot = 0;

        while(tankIterator.hasNext()) {
            slot++;
            StorageView<FluidVariant> view = tankIterator.next();

            if(slot == tank) {
                return new FluidStack(view);
            };
        }

        return FluidStack.EMPTY;
    };
}
