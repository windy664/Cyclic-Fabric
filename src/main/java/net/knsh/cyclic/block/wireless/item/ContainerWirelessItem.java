package net.knsh.cyclic.block.wireless.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ContainerWirelessItem extends ContainerBase {
    protected TileWirelessItem tile;

    public ContainerWirelessItem(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.WIRELESS_ITEM, windowId);
        tile = (TileWirelessItem) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = tile.inventory.getSlotCount() + 1; //+1 for output slot
        addSlot(new SlotItemHandler(tile.gpsSlots, 0, 80, 36) {

            @Override
            public void setChanged() {
                tile.setChanged();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new SlotItemHandler(tile.inventory, 0, 143, 36));
        layoutPlayerInventorySlots(8, 84);
        this.trackAllIntFields(tile, TileWirelessItem.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.WIRELESS_ITEM.block());
    }
}
