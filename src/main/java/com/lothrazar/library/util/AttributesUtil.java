package com.lothrazar.library.util;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class AttributesUtil {
    public static void removePlayerReach(UUID id, Player player) {
        AttributeInstance attr = player.getAttribute(ReachEntityAttributes.REACH);
        attr.removeModifier(id);
    }
    public static void setPlayerReach(UUID id, Player player, int reachBoost) {
        removePlayerReach(id, player);
        AttributeInstance attr = player.getAttribute(ReachEntityAttributes.REACH);
        //vanilla is 5, so +11 it becomes 16
        AttributeModifier enchantment = new AttributeModifier(id, "ReachFLIB", reachBoost, AttributeModifier.Operation.ADDITION);
        attr.addPermanentModifier(enchantment);
    }
}
