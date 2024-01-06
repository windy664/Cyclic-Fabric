package net.knsh.cyclic.block.cable.fluid;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class ContainerCableFluid extends ContainerBase {
    protected TileCableFluid tile;

    public ContainerCableFluid(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.FLUID_PIPE, windowId);
        tile = (TileCableFluid) world.getBlockEntity(pos);
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
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.FLUID_PIPE.block());
    }
}
