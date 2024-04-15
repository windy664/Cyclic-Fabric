package com.lothrazar.cyclic.network;

import com.lothrazar.cyclic.ModCyclic;
import net.minecraft.resources.ResourceLocation;

public class PacketIdentifiers {
    public static final ResourceLocation TILE_DATA = new ResourceLocation(ModCyclic.MODID, "packet_tile_data");
    public static final ResourceLocation FLUID_DATA = new ResourceLocation(ModCyclic.MODID, "packet_fluid_data");
    public static final ResourceLocation SYNC_FLUID = new ResourceLocation(ModCyclic.MODID, "packet_sync_fluid");
    public static final ResourceLocation SYNC_ENERGY = new ResourceLocation(ModCyclic.MODID, "packet_sync_energy");
    public static final ResourceLocation CRAFT_ACTION = new ResourceLocation(ModCyclic.MODID, "packet_craft_action");
}
