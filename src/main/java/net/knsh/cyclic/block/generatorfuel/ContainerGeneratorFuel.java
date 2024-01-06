package net.knsh.cyclic.block.generatorfuel;

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

public class ContainerGeneratorFuel extends ContainerBase {
    TileGeneratorFuel tile;

    public ContainerGeneratorFuel(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(CyclicScreens.GENERATOR_FUEL, windowId);
        tile = (TileGeneratorFuel) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        addSlot(new SlotItemHandler(tile.inputSlots, 0, 75, 35) {

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });
        //    addSlot(new SlotItemHandler(tile.outputSlots, 0, 109, 35));
        this.endInv = tile.inputSlots.getSlotCount();
        layoutPlayerInventorySlots(8, 84);
        this.trackAllIntFields(tile, TileGeneratorFuel.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, CyclicBlocks.GENERATOR_FUEL.block());
    }
}
