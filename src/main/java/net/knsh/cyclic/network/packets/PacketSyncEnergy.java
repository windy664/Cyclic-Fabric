package net.knsh.cyclic.network.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.knsh.cyclic.library.core.IHasEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketSyncEnergy {
    private BlockPos pos;
    private long energy;

    public PacketSyncEnergy(BlockPos p, long fluid) {
        pos = p;
        this.energy = fluid;
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PacketSyncEnergy message = decode(buf);
        client.execute(() -> {
            doWork(message);
        });
    }

    private static void doWork(PacketSyncEnergy message) {
        BlockEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);
        if (te instanceof IHasEnergy tile) {
            tile.setEnergy(message.energy);
        }
    }

    public static PacketSyncEnergy decode(FriendlyByteBuf buf) {
        PacketSyncEnergy msg = new PacketSyncEnergy(buf.readBlockPos(),
                buf.readLong());
        return msg;
    }

    public static FriendlyByteBuf encode(PacketSyncEnergy msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(msg.pos);
        buf.writeLong(msg.energy);
        return buf;
    }
}
