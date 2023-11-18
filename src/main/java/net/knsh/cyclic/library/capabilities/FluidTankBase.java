package net.knsh.cyclic.library.capabilities;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class FluidTankBase extends SingleVariantStorage<FluidVariant> {
    public String fluidBlockIdentifier;
    private BlockEntityCyclic tile;
    private int capacity;

    public FluidTankBase(BlockEntityCyclic tile, int capacity) {
        super();
        this.tile = tile;
        this.capacity = capacity;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return this.capacity;
    }

    @Override
    protected void onFinalCommit() {
        tile.setChanged();
        if (this.isResourceBlank()) {
            return;
        }
        FluidVariant f = this.getResource();
        if (!tile.getLevel().isClientSide) {
            PlayerLookup.all(tile.getLevel().getServer()).forEach((serverPlayerEntity -> {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(tile.getBlockPos());
                CompoundTag nbt = new CompoundTag();
                if (!this.isResourceBlank()) {
                    this.writeNbt(nbt);
                }
                buf.writeNbt(nbt);
                buf.writeLong(this.amount);

                ServerPlayNetworking.send(serverPlayerEntity, PacketIdentifiers.FLUID_DATA, buf);
            }));
        }
    }
}
