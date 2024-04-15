package com.lothrazar.cyclic.block.wireless.energy;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicScreens;
import com.lothrazar.library.core.Const;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ContainerWirelessEnergy extends ContainerBase {
    protected TileWirelessEnergy tile;

    public ContainerWirelessEnergy(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.WIRELESS_ENERGY, windowId);
        tile = (TileWirelessEnergy) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = tile.gpsSlots.getSlotCount();
        for (int j = 0; j < tile.gpsSlots.getSlotCount(); j++) {
            int row = j / 9;
            int col = j % 9;
            int xPos = 8 + col * Const.SQ;
            int yPos = 36 + row * Const.SQ;
            addSlot(new SlotItemHandler(tile.gpsSlots, j, xPos, yPos) {
                @Override
                public void setChanged() {
                    tile.setChanged();
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
        layoutPlayerInventorySlots(8, 84);
        this.trackAllIntFields(tile, TileWirelessEnergy.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.WIRELESS_ENERGY.block());
    }
}
