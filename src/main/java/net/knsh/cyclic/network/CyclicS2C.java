package net.knsh.cyclic.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.knsh.cyclic.library.core.IHasFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CyclicS2C {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.FLUID_DATA, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            FluidVariant fluid = FluidVariant.fromNbt(buf.readNbt());
            long amount = buf.readLong();

            client.execute(() -> {
                BlockEntity blockEntity = client.level.getBlockEntity(pos);
                if (blockEntity instanceof IHasFluid fluidBlockEntity) {
                    fluidBlockEntity.setFluid(fluid);
                    fluidBlockEntity.setFluidAmount(amount);
                }
            });
        });
    }
}
