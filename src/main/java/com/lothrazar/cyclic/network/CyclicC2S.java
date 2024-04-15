package com.lothrazar.cyclic.network;

import com.lothrazar.cyclic.item.datacard.filter.PacketFilterCard;
import com.lothrazar.cyclic.network.packets.PacketTileData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.lothrazar.cyclic.network.packets.PacketCraftAction;

public class CyclicC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.TILE_DATA, PacketTileData::handle);
        ServerPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CRAFT_ACTION, PacketCraftAction::handle);
        ServerPlayNetworking.registerGlobalReceiver(PacketFilterCard.IDENTIFIER, PacketFilterCard::handle);
    }
}
