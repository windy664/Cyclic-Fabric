package net.knsh.cyclic.porting.neoforge.network;

import net.knsh.cyclic.mixin.common.ServerPlayerAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class NetworkHooks {
    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier) {
        if (player.level().isClientSide) return;
        player.doCloseContainer();
        ((ServerPlayerAccessor) player).callNextContainerCounter();
        int openContainerId = ((ServerPlayerAccessor) player).getContainerCounter();

        var c = containerSupplier.createMenu(openContainerId, player.getInventory(), player);
        if (c == null)
            return;

        player.containerMenu = c;
        ((ServerPlayerAccessor) player).callInitMenu(player.containerMenu);
    }
}
