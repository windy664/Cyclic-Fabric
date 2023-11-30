package net.knsh.cyclic.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.library.util.SoundUtil;
import net.knsh.cyclic.registry.CyclicItems;
import net.knsh.cyclic.registry.CyclicSounds;
import net.minecraft.world.InteractionResult;

public class ItemEvents {
    public static void register() {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            if (player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;

            //TODO scaffolding

            if (player.isCrouching() && player.getItemInHand(hand).is(CyclicItems.CABLE_WRENCH)) {
                if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CableBase) {
                    player.swing(hand);
                    CableBase.crouchClick(world.getBlockState(hitResult.getBlockPos()), world, hitResult.getBlockPos(), player, hitResult);
                    SoundUtil.playSound(player, CyclicSounds.THUNK, 0.2F, 1F);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.PASS;
        }));
    }
}
