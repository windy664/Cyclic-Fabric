package net.knsh.cyclic.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CyclicC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.TILE_DATA, ((server, player, handler, buf, responseSender) -> {
            int field = buf.readInt();
            BlockPos blockPos = buf.readBlockPos();
            boolean increment = buf.readBoolean();

            server.execute(() -> {
                BlockEntity blockEntity = player.getCommandSenderWorld().getBlockEntity(blockPos);

                if (blockEntity instanceof BlockEntityCyclic blockEntityCyclic) {
                    if (increment) {
                        int incr = blockEntityCyclic.getField(field) + 1;
                        blockEntityCyclic.setField(field, incr);
                    }
                    blockEntityCyclic.setChanged();
                }
            });
        }));
    }
}
