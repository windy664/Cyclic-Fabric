package net.knsh.cyclic.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.knsh.cyclic.network.packets.PacketTileData;

public class CyclicC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.TILE_DATA, PacketTileData::handle);
    }
}
