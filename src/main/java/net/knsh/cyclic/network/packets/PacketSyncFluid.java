package net.knsh.cyclic.network.packets;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.knsh.cyclic.Cyclic;
import net.knsh.flib.core.IHasFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketSyncFluid {
    private final BlockPos pos;
    private final FluidStack fluid;

    public PacketSyncFluid(BlockPos p, FluidStack fluid) {
        pos = p;
        this.fluid = fluid;
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PacketSyncFluid message = decode(buf);
        client.execute(() -> {
            doWork(message);
        });
    }

    private static void doWork(PacketSyncFluid message) {
        BlockEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);
        if (te instanceof IHasFluid tile) {
            Cyclic.LOGGER.info("Setting fluid");
            tile.setFluid(message.fluid);
        }
    }

    public static PacketSyncFluid decode(FriendlyByteBuf buf) {
        PacketSyncFluid msg = new PacketSyncFluid(buf.readBlockPos(),
                FluidStack.loadFluidStackFromNBT(buf.readNbt()));
        return msg;
    }

    public static FriendlyByteBuf encode(PacketSyncFluid msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(msg.pos);
        CompoundTag tags = new CompoundTag();
        if (msg.fluid != null) {
            msg.fluid.writeToNBT(tags);
        }
        buf.writeNbt(tags);
        return buf;
    }
}
