package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.knsh.cyclic.Cyclic;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CyclicLootModifiers {
    public static void register() {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
            if (CyclicEnchants.AUTOSMELT.getDescriptionId().equals(id)) {
                Cyclic.LOGGER.info("AUTOSMELT enchantment detected");
            }
            return null;
        });
    }
}
