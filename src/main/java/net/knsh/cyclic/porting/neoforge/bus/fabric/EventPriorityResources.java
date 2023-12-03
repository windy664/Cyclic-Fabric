package net.knsh.cyclic.porting.neoforge.bus.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.cyclic.Cyclic;
import net.minecraft.resources.ResourceLocation;

public class EventPriorityResources {
    public static final ResourceLocation HIGHEST = new ResourceLocation(Cyclic.MOD_ID, "highest");
    public static final ResourceLocation HIGH = new ResourceLocation(Cyclic.MOD_ID, "high");
    public static final ResourceLocation NORMAL = Event.DEFAULT_PHASE;
    public static final ResourceLocation LOW = new ResourceLocation(Cyclic.MOD_ID, "low");
    public static final ResourceLocation LOWEST = new ResourceLocation(Cyclic.MOD_ID, "lowest");
}
