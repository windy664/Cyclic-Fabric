package net.knsh.cyclic.library.capabilities;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.lookups.types.FluidLookup;
import net.knsh.cyclic.network.CyclicS2C;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.knsh.cyclic.network.packets.PacketSyncFluid;

import java.util.function.Predicate;

public class ForgeFluidTankBase extends FluidTank {
    private BlockEntityCyclic tile;

    public ForgeFluidTankBase(BlockEntityCyclic tile, int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.tile = tile;
    }

    @Override
    protected void onContentsChanged() {
        FluidLookup lookup = CyclicLookup.FLUID_HANDLER.find(tile.getLevel(), tile.getBlockPos(), null);
        if (lookup == null || lookup.getFluidTank().getFluid() == null) {
            return;
        }
        FluidStack f = lookup.getFluidTank().getFluid();
        if (tile.getLevel().isClientSide == false) { //if serverside then
            CyclicS2C.sendToAllClients(tile.getLevel(), PacketSyncFluid.encode(new PacketSyncFluid(tile.getBlockPos(), f)), PacketIdentifiers.SYNC_FLUID);
        }
        super.onContentsChanged();
    }

    public long fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (stack.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!stack.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - stack.getAmount(), resource.getAmount());
        }
        if (stack.isEmpty()) {
            stack = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return stack.getAmount();
        }
        if (!getFluid().isFluidEqual(resource)) {
            return 0;
        }
        long filled = capacity - getFluid().getAmount();

        if (resource.getAmount() < filled) {
            stack.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            stack.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }
}
