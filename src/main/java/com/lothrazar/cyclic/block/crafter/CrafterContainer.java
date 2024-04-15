package com.lothrazar.cyclic.block.crafter;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.flib.core.Const;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrafterContainer extends ContainerBase {
    CrafterBlockEntity tile;
    public static final int INPUT_START_X = 8;
    public static final int INPUT_START_Y = 35;
    public static final int GRID_START_X = 52;
    public static final int GRID_START_Y = 71;
    public static final int OUTPUT_START_X = 114;
    public static final int OUTPUT_START_Y = 35;
    public static final int PREVIEW_START_X = 70;
    public static final int PREVIEW_START_Y = 35;
    private final Container inventory;

    public CrafterContainer(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(21), world, pos);
    }

    public CrafterContainer(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.CRAFTER, syncId);
        tile = (CrafterBlockEntity) world.getBlockEntity(pos);
        this.endInv = CrafterBlockEntity.IO_NUM_COLS * CrafterBlockEntity.IO_NUM_ROWS;
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;
        this.inventory = inventorysent;
        int indexx;

        // add input
        indexx = CrafterBlockEntity.INPUT;
        for (int rowPos = 0; rowPos < CrafterBlockEntity.IO_NUM_ROWS; rowPos++) {
            for (int colPos = 0; colPos < CrafterBlockEntity.IO_NUM_COLS; colPos++) {
                this.addSlot(new Slot(inventory, indexx, INPUT_START_X + colPos * Const.SQ, INPUT_START_Y + rowPos * Const.SQ) {
                    @Override
                    public void setChanged() {
                        tile.setChanged();
                    }
                });
                indexx++;
            }
        }

        // add grid
        int gridIndex = 0;
        for (int rowPos = 0; rowPos < CrafterBlockEntity.GRID_NUM_ROWS; rowPos++) {
            for (int colPos = 0; colPos < CrafterBlockEntity.GRID_NUM_ROWS; colPos++) {
                this.addSlot(new Slot(tile.grid, gridIndex, GRID_START_X + colPos * Const.SQ, GRID_START_Y + rowPos * Const.SQ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return true;
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        return false;
                    }

                    @Override
                    public void set(ItemStack stack) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        super.set(copy);
                        stack.grow(1);
                    }

                    @Override
                    public void setChanged() {
                        tile.setChanged();
                    }
                });
                gridIndex++;
            }
        }

        // add output
        indexx = CrafterBlockEntity.OUTPUT;
        for (int rowPos = 0; rowPos < CrafterBlockEntity.IO_NUM_ROWS; rowPos++) {
            for (int colPos = 0; colPos < CrafterBlockEntity.IO_NUM_COLS; colPos++) {
                this.addSlot(new Slot(inventory, indexx, OUTPUT_START_X + colPos * Const.SQ, OUTPUT_START_Y + rowPos * Const.SQ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        return true;
                    }

                    @Override
                    public void set(ItemStack stack) {
                        super.set(stack);
                    }

                    @Override
                    public void setChanged() {
                        tile.setChanged();
                    }
                });
                indexx++;
            }
        }

        // add preview
        indexx = CrafterBlockEntity.PREVIEW;
        addSlot(new Slot(inventory, indexx, PREVIEW_START_X, PREVIEW_START_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public boolean mayPickup(Player player) {
                return false;
            }

            @Override
            public void set(ItemStack stack) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                super.set(copy);
                stack.grow(1);
            }

            @Override
            public void setChanged() {
                tile.setChanged();
            }
        });
        this.endInv = slots.size();
        layoutPlayerInventorySlots(playerInventory, 8, 153);
        this.trackAllIntFields(tile, CrafterBlockEntity.Fields.values().length);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        int playerStart = endInv;
        int playerEnd = endInv + PLAYERSIZE;
        //standard logic based on start/end
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            //from output to player
            if (index >= CrafterBlockEntity.OUTPUT_SLOT_START && index <= CrafterBlockEntity.OUTPUT_SLOT_STOP) {
                if (!this.moveItemStackTo(stack, playerStart, playerEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
            //from input to player
            if (index < CrafterBlockEntity.IO_SIZE) {
                if (!this.moveItemStackTo(stack, playerStart, playerEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index <= playerEnd && !this.moveItemStackTo(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId == CrafterBlockEntity.PREVIEW_SLOT) {
            return; //ItemStack.EMPTY;
        }
        // [ 10 - 18 ]
        if (slotId >= CrafterBlockEntity.GRID_SLOT_START && slotId <= CrafterBlockEntity.GRID_SLOT_STOP) {
            ItemStack ghostStack = player.containerMenu.getCarried().copy();
            ghostStack.setCount(1);
            slots.get(slotId).set(ghostStack);
            //      tile.shouldSearch = true;
            return; //ItemStack.EMPTY;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.getContainerSlot() != CrafterBlockEntity.PREVIEW_SLOT &&
                !(slotIn.getContainerSlot() >= CrafterBlockEntity.GRID_SLOT_START && slotIn.getContainerSlot() <= CrafterBlockEntity.GRID_SLOT_STOP) &&
                super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
