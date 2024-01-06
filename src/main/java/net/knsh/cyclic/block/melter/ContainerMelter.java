package net.knsh.cyclic.block.melter;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class ContainerMelter extends ContainerBase {
    TileMelter tile;

    public ContainerMelter(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.MELTER, windowId);
        tile = (TileMelter) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        SlottedStackStorage h = CyclicLookup.ITEM_HANDLER.find(world, pos, null);
        if (h != null) {
            this.endInv = h.getSlotCount();
            addSlot(new SlotItemHandler(h, 0, 17, 31) {

                @Override
                public void setChanged() {
                    tile.setChanged();
                }
            });
            addSlot(new SlotItemHandler(h, 1, 35, 31) {

                @Override
                public void setChanged() {
                    tile.setChanged();
                }
            });
        }
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields(tile, TileMelter.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.MELTER.block());
    }
}
