/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lothrazar.cyclic.event.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;

public interface BeforeDamageCallback {
    /**
     * An event that is called when a living entity is going to take damage, allows adjustment of damage taken.
     * This is fired from LivingEntity, before armor or any other mitigation are applied.
     * Mods can adjust this to change the damage the entity takes.
     */
    Event<BeforeDamageCallback> BEFORE_DAMAGE = EventFactory.createArrayBacked(BeforeDamageCallback.class, (listeners) -> (source, amount) -> {
        for (BeforeDamageCallback listener : listeners) {
            amount = listener.beforeDamage(source, amount);
        }
        return amount;
    });

    float beforeDamage(DamageSource source, float amount);
}
