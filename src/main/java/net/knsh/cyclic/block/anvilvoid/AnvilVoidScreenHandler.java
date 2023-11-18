package net.knsh.cyclic.block.anvilvoid;

import net.knsh.cyclic.gui.ScreenHandlerBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AnvilVoidScreenHandler extends ScreenHandlerBase {
    private final Container inventory;
    AnvilVoidBlockEntity blockEntity;

    public AnvilVoidScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public AnvilVoidScreenHandler(int syncId, Inventory playerInventory, BlockEntity blockEntity) {
        super(CyclicScreens.ANVIL_VOID, syncId);
        checkContainerSize((Container) blockEntity, 2);
        this.inventory = (Container) blockEntity;
        this.playerInventory = playerInventory;
        inventory.startOpen(playerInventory.player);
        this.blockEntity = ((AnvilVoidBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 55, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK || (stack.getTag() != null && stack.getTag().contains("Enchantments"));
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                blockEntity.setChanged();
                super.setByPlayer(stack);
            }
        });
        this.addSlot(new Slot(inventory, 1, 109, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                blockEntity.setChanged();
                super.setByPlayer(stack);
            }
        });

        this.endInv = 2;
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields((AnvilVoidBlockEntity) blockEntity, AnvilVoidBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
