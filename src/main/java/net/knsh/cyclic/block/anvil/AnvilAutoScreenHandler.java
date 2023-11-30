package net.knsh.cyclic.block.anvil;

import net.knsh.cyclic.gui.ScreenHandlerBase;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnvilAutoScreenHandler extends ScreenHandlerBase {
    AnvilAutoBlockEntity tile;
    private final Container inventory;

    public AnvilAutoScreenHandler(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(2), world, pos);
    }

    protected AnvilAutoScreenHandler(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.ANVIL, syncId);
        tile = (AnvilAutoBlockEntity) world.getBlockEntity(pos);
        this.inventory = inventorysent;
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;

        addSlot(new Slot(inventorysent, 0, 55, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty() && stack.getDamageValue() != 0;
            }

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });
        addSlot(new Slot(inventorysent, 1, 109, 35) {
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
        this.trackAllIntFields(tile, AnvilAutoBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
