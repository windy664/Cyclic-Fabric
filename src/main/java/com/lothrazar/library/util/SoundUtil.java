package com.lothrazar.library.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SoundUtil {
    public static void playSoundAtBlock(Level world, Entity player, BlockPos pos, SoundEvent soundIn) {
        world.playSound(player, pos, soundIn, SoundSource.BLOCKS, 1, 1);
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent soundIn) {
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, 0.5F, 0.5F, false);
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent soundIn, float volume) {
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, volume, 0.5F, false);
    }

    public static void playSound(Entity entityIn, SoundEvent soundIn) {
        playSound(entityIn, soundIn, 1.0F, 1.0F);
    }

    public static void playSound(Entity entityIn, SoundEvent soundIn, float volume) {
        playSound(entityIn, soundIn, volume, 1.0F);
    }

    public static void playSound(Entity entityIn, SoundEvent soundIn, float volume, float pitch) {
        if (entityIn != null && entityIn.level().isClientSide) {
            entityIn.playSound(soundIn, volume, pitch);
        }
    }

    public static void playSoundFromServer(ServerPlayer entityIn, BlockPos pos, SoundEvent soundIn, float vol, float pitch) {
        if (soundIn == null || entityIn == null) {
            return;
        }
        entityIn.connection.send(new ClientboundSoundPacket(
                Holder.direct(soundIn),
                SoundSource.BLOCKS,
                pos.getX(), pos.getY(), pos.getZ(),
                vol, pitch, entityIn.level().getRandom().nextLong()));
    }

    public static void playSoundFromServer(ServerPlayer entityIn, SoundEvent soundIn, float vol, float pitch) {
        if (soundIn == null || entityIn == null) {
            return;
        }
        entityIn.connection.send(new ClientboundSoundPacket(
                Holder.direct(soundIn),
                SoundSource.BLOCKS,
                entityIn.xOld, entityIn.yOld, entityIn.zOld,
                vol, pitch, entityIn.level().getRandom().nextLong()));
    }

    public static void playSoundFromServer(ServerLevel world, BlockPos pos, SoundEvent soundIn) {
        for (ServerPlayer sp : world.players()) {
            playSoundFromServer(sp, pos, soundIn, 1F, 1F);
        }
    }
}
