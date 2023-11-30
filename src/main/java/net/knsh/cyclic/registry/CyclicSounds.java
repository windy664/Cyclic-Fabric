package net.knsh.cyclic.registry;

import net.knsh.cyclic.Cyclic;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class CyclicSounds {
    public static SoundEvent THUNK = registerSound("thunk");

    private static SoundEvent registerSound(String id) {
        ResourceLocation identifier = new ResourceLocation(Cyclic.MOD_ID, id);
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(identifier);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, soundEvent);
    }

    public static void register() {}
}
