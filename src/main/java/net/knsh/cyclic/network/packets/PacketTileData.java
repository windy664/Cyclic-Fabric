package net.knsh.cyclic.network.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketTileData {

    private int field;
    private int value;
    private BlockPos pos;
    private boolean autoIncrement = false;

    public PacketTileData(int field, BlockPos pos) {
        super();
        this.field = field;
        this.value = -1;
        this.autoIncrement = true;
        this.pos = pos;
    }

    public PacketTileData(int field, int value, BlockPos pos) {
        super();
        this.field = field;
        this.value = value;
        this.autoIncrement = false;
        this.pos = pos;
    }

    public PacketTileData(int field, boolean value, BlockPos pos) {
        this(field, value ? 1 : 0, pos);
    }

    public PacketTileData() {}

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PacketTileData message = decode(buf);
        server.execute(() -> {
            Level world = player.getCommandSenderWorld();
            BlockEntity tile = world.getBlockEntity(message.pos);
            if (tile instanceof BlockEntityCyclic) {
                BlockEntityCyclic base = (BlockEntityCyclic) tile;
                if (message.autoIncrement) {
                    int incr = base.getField(message.field) + 1;
                    base.setField(message.field, incr);
                }
                else {
                    base.setField(message.field, message.value);
                }
                base.setChanged();
            }
        });
    }

    public static PacketTileData decode(FriendlyByteBuf buf) {
        PacketTileData p = new PacketTileData();
        p.field = buf.readInt();
        p.value = buf.readInt();
        p.pos = buf.readBlockPos();
        p.autoIncrement = buf.readBoolean();
        return p;
    }

    public static FriendlyByteBuf encode(PacketTileData msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(msg.field);
        buf.writeInt(msg.value);
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.autoIncrement);
        return buf;
    }
}
