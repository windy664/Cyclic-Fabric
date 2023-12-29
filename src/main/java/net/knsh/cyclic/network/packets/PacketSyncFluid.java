package net.knsh.cyclic.network.packets;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.knsh.cyclic.library.core.IHasFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketSyncFluid {
    private BlockPos pos;
    private FluidStack fluid;

    public PacketSyncFluid(BlockPos p, FluidStack fluid) {
        pos = p;
        this.fluid = fluid;
    }

    public static void handle(MinecraftServer server, ServerPlayer sender, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PacketSyncFluid message = decode(buf);
        server.execute(() -> {
            doWork(message);
        });
    }

    private static void doWork(PacketSyncFluid message) {
        BlockEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);
        if (te instanceof IHasFluid tile) {
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
