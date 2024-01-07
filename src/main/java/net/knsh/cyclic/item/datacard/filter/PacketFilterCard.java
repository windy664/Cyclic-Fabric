package net.knsh.cyclic.item.datacard.filter;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.data.CraftingActionEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketFilterCard {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Cyclic.MOD_ID, "packet_filter_card");

    private final CraftingActionEnum action;

    public PacketFilterCard(CraftingActionEnum s) {
        action = s;
    }

    public static PacketFilterCard decode(FriendlyByteBuf buf) {
        return new PacketFilterCard(CraftingActionEnum.values()[buf.readInt()]);
    }

    public static FriendlyByteBuf encode(PacketFilterCard msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(msg.action.ordinal());
        return buf;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            //rotate type
            ItemStack filter = player.getItemInHand(InteractionHand.MAIN_HAND);
            FilterCardItem.toggleFilterType(filter);
        });
    }
}
