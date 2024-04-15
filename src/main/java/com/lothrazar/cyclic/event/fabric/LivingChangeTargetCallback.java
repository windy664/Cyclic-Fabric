package com.lothrazar.cyclic.event.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public interface LivingChangeTargetCallback {
    Event<LivingChangeTargetCallback> EVENT = EventFactory.createArrayBacked(LivingChangeTargetCallback.class,
        (listeners) -> (target, originalTarget, canceled) -> {
            for (LivingChangeTargetCallback event : listeners) {
                canceled = event.onLivingChangeTarget(target, originalTarget, canceled) ;
            }
            return canceled;
        }
    );

    Boolean onLivingChangeTarget(LivingEntity target, LivingEntity originalTarget, boolean canceled);
}
