package net.knsh.cyclic.block.cable.item;

import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class ItemCableContainer extends ContainerBase {
    ItemCableBlockEntity tile;
    private final Container inventory;

    public ItemCableContainer(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(1), world, pos);
    }

    public ItemCableContainer(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.ITEM_PIPE, syncId);
        tile = (ItemCableBlockEntity) world.getBlockEntity(pos);
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;
        this.inventory = inventorysent;
        this.endInv = inventorysent.getContainerSize();

        addSlot(new Slot(inventory, 0, 80, 29) {

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });

        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
