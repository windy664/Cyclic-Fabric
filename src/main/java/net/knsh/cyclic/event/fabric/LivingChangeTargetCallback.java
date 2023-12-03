package net.knsh.cyclic.event.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public interface LivingChangeTargetCallback {
    Event<LivingChangeTargetCallback> EVENT = EventFactory.createArrayBacked(LivingChangeTargetCallback.class, (listeners) -> (target) -> {
        for (LivingChangeTargetCallback event : listeners) {
            target = event.onLivingChangeTarget(target);
        }
        return target;
    });

    LivingEntity onLivingChangeTarget(LivingEntity target);
}
