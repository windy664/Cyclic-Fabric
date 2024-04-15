package com.lothrazar.library.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class FakePlayerUtil {
    private static final UUID ID = UUID.randomUUID();

    public static boolean isFakePlayer(Entity attacker) {
        return attacker instanceof FakePlayer;
    }

    public static WeakReference<FakePlayer> initFakePlayer(ServerLevel ws, String blockName) {
        final String name = "fake_player." + blockName;
        final GameProfile breakerProfile = new GameProfile(ID, name);
        WeakReference<FakePlayer> fakePlayer = new WeakReference<FakePlayer>(FakePlayer.get(ws, breakerProfile));
        if (fakePlayer == null || fakePlayer.get() == null) {
            fakePlayer = null;
            return null; // trying to get around https://github.com/PrinceOfAmber/Cyclic/issues/113
        }
        fakePlayer.get().setOnGround(true);
        //    fakePlayer.get().onGround = true;
        fakePlayer.get().connection = new ServerGamePacketListenerImpl(ws.getServer(), new Connection(PacketFlow.SERVERBOUND), fakePlayer.get()) {
            @Override
            public void send(Packet<?> packet) {}
        };
        fakePlayer.get().setSilent(true);
        return fakePlayer;
    }

    public static InteractionResult interactUseOnBlock(WeakReference<FakePlayer> fakePlayer,
                                                  Level world, BlockPos targetPos, InteractionHand hand, Direction facing) throws Exception {
        if (fakePlayer == null) {
            return InteractionResult.FAIL;
        }
        Direction placementOn = (facing == null) ? fakePlayer.get().getMotionDirection() : facing;
        BlockHitResult blockraytraceresult = new BlockHitResult(
                fakePlayer.get().getLookAngle(), placementOn,
                targetPos, true);
        //processRightClick
        ItemStack itemInHand = fakePlayer.get().getItemInHand(hand);
        InteractionResult result = fakePlayer.get().gameMode.useItemOn(fakePlayer.get(), world, itemInHand, hand, blockraytraceresult);
        // ModCyclic.LOGGER.info(targetPos + " gameMode.useItemOn() result = " + result + "  itemInHand = " + itemInHand);
        //it becomes CONSUME result 1 bucket. then later i guess it doesnt save, and then its water_bucket again
        return result;
    }

    public static void tryEquipItem(ItemStack item, WeakReference<FakePlayer> fp, InteractionHand hand) {
        if (fp == null) {
            return;
        }
        fp.get().setItemInHand(hand, item);
    }

    public static void syncEquippedItem(InventoryStorage inv, WeakReference<FakePlayer> fp, int slot, InteractionHand hand) {
        if (fp == null) {
            return;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            inv.getSlot(slot).insert(ItemVariant.of(ItemStack.EMPTY), 1, transaction);
            inv.getSlot(slot).insert(ItemVariant.of(fp.get().getItemInHand(hand)), 64, transaction);
            transaction.commit();
        }
        //    inv.extractItem(slot, 64, false); //delete and overwrite
    }
}
