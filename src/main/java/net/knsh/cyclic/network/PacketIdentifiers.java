package net.knsh.cyclic.network;

import net.knsh.cyclic.Cyclic;
import net.minecraft.resources.ResourceLocation;

public class PacketIdentifiers {
    public static final ResourceLocation TILE_DATA = new ResourceLocation(Cyclic.MOD_ID, "packet_tile_data");
    public static final ResourceLocation FLUID_DATA = new ResourceLocation(Cyclic.MOD_ID, "packet_fluid_data");
    public static final ResourceLocation SYNC_FLUID = new ResourceLocation(Cyclic.MOD_ID, "packet_sync_fluid");
    public static final ResourceLocation SYNC_ENERGY = new ResourceLocation(Cyclic.MOD_ID, "packet_sync_energy");
    public static final ResourceLocation CRAFT_ACTION = new ResourceLocation(Cyclic.MOD_ID, "packet_craft_action");
}
