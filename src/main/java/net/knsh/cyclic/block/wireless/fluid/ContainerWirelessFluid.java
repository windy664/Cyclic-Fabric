package net.knsh.cyclic.block.wireless.fluid;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ContainerWirelessFluid extends ContainerBase {
    protected TileWirelessFluid tile;

    public ContainerWirelessFluid(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.WIRELESS_FLUID, windowId);
        tile = (TileWirelessFluid) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = tile.gpsSlots.getSlotCount();
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
        layoutPlayerInventorySlots(8, 84);
        this.trackAllIntFields(tile, TileWirelessFluid.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.WIRELESS_FLUID.block());
    }
}
