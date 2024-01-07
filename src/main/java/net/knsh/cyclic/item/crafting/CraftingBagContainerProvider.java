package net.knsh.cyclic.item.crafting;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class CraftingBagContainerProvider implements ExtendedScreenHandlerFactory {
    private final int slot;

    public CraftingBagContainerProvider(int s) {
        this.slot = s;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.cyclic.crafting_bag");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CraftingBagContainer(i, inventory, player, slot);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }
}
