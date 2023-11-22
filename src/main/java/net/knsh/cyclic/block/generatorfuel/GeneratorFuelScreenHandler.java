package net.knsh.cyclic.block.generatorfuel;

import net.knsh.cyclic.gui.ScreenHandlerBase;
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

public class GeneratorFuelScreenHandler extends ScreenHandlerBase {
    GeneratorFuelBlockEntity tile;
    private final Container inventory;

    public GeneratorFuelScreenHandler(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(1), world, pos);
    }

    public GeneratorFuelScreenHandler(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.GENERATOR_FUEL, syncId);
        tile = (GeneratorFuelBlockEntity) world.getBlockEntity(pos);
        this.inventory = inventorysent;
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;

        this.addSlot(new Slot(inventory, 0, 75, 35) {
            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });

        this.endInv = inventory.getContainerSize();
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields(tile, GeneratorFuelBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
