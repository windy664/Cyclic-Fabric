package net.knsh.cyclic.block.cable.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ContainerCableItem extends ContainerBase {
    protected TileCableItem tile;

    public ContainerCableItem(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.ITEM_PIPE, windowId);
        tile = (TileCableItem) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = tile.filter.getSlotCount();

        addSlot(new SlotItemHandler(tile.filter, 0, 80, 29) {
            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.ITEM_PIPE.block());
    }
}
