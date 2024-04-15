package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.ModCyclic;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class CyclicSounds {
    public static SoundEvent THUNK = registerSound("thunk");

    private static SoundEvent registerSound(String id) {
        ResourceLocation identifier = new ResourceLocation(ModCyclic.MODID, id);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(identifier);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, soundEvent);
    }

    public static void register() {}
}
