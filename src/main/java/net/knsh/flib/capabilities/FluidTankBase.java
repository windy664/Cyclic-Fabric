package net.knsh.flib.capabilities;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.network.CyclicS2C;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.knsh.cyclic.network.packets.PacketSyncFluid;

import java.util.function.Predicate;

public class FluidTankBase extends FluidTank {
    private final BlockEntityCyclic tile;

    public FluidTankBase(BlockEntityCyclic tile, long capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.tile = tile;
    }

    @Override
    protected void onContentsChanged() {
        Storage<FluidVariant> handler = FluidStorage.SIDED.find(tile.getLevel(), tile.getBlockPos(), null);
        if (handler instanceof FluidTank tank) {
            if (handler == null || tank.getFluid() == null) {
                return;
            }
            FluidStack f = tank.getFluid();
            if (!tile.getLevel().isClientSide) { //if serverside then
                CyclicS2C.sendToAllClients(tile.getLevel(), PacketSyncFluid.encode(new PacketSyncFluid(tile.getBlockPos(), f)), PacketIdentifiers.SYNC_FLUID);
            }
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
            setFluid(new FluidStack(resource, Math.min(capacity, resource.getAmount())));
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
