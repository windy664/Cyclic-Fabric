package com.lothrazar.cyclic.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.lothrazar.cyclic.network.packets.PacketSyncEnergy;
import com.lothrazar.cyclic.network.packets.PacketSyncFluid;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CyclicS2C {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.SYNC_FLUID, PacketSyncFluid::handle);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.SYNC_ENERGY, PacketSyncEnergy::handle);

        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.FLUID_DATA, (client, handler, buf, responseSender) -> {});
    }

    public static void sendToAllClients(Level level, FriendlyByteBuf buf, ResourceLocation packetId) {
        if (level.isClientSide()) return;

        for (Player player : level.players()) {
            ServerPlayer sp = ((ServerPlayer) player);
            ServerPlayNetworking.send(sp, packetId, buf);
        }
    }
}
