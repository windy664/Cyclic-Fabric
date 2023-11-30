package net.knsh.cyclic.block.anvilmagma;

import net.knsh.cyclic.gui.ScreenHandlerBase;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnvilMagmaScreenHandler extends ScreenHandlerBase {
    AnvilMagmaBlockEntity tile;
    private final Container inventory;

    public AnvilMagmaScreenHandler(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(2), world, pos);
    }

    public AnvilMagmaScreenHandler(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.ANVIL_MAGMA, syncId);
        tile = (AnvilMagmaBlockEntity) world.getBlockEntity(pos);
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;
        this.inventory = inventorysent;

        addSlot(new Slot(inventory, 0, 55, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty() && stack.getDamageValue() != 0;
            }

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });
        addSlot(new Slot(inventory, 1, 109, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });

        this.endInv = 2;
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields(tile, AnvilMagmaBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
