package net.knsh.cyclic.gui;

import net.knsh.cyclic.block.BlockEntityCyclic;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ContainerBase extends AbstractContainerMenu {
    public static final int PLAYERSIZE = 4 * 9;
    protected Player playerEntity;
    protected Inventory playerInventory;
    protected int startInv = 0;
    protected int endInv = 17;

    protected ContainerBase(MenuType<?> type, int id) {
        super(type, id);
    }

    protected void trackAllIntFields(BlockEntityCyclic tile, int fieldCount) {
        for (int f = 0; f < fieldCount; f++) {
            trackIntField(tile, f);
        }
    }

    protected void trackIntField(BlockEntityCyclic tile, int fieldOrdinal) {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return tile.getField(fieldOrdinal);
            }

            @Override
            public void set(int value) {
                tile.setField(fieldOrdinal, value);
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        try {
            //if last machine slot is 17, endInv is 18
            int playerStart = endInv;
            int playerEnd = endInv + PLAYERSIZE; //53 = 17 + 36
            //standard logic based on start/end
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.slots.get(index);
            if (slot != null && slot.hasItem()) {
                ItemStack stack = slot.getItem();
                itemstack = stack.copy();
                if (index < this.endInv) {
                    if (!this.moveItemStackTo(stack, playerStart, playerEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index <= playerEnd && !this.moveItemStackTo(stack, startInv, endInv, false)) {
                    return ItemStack.EMPTY;
                }
                if (stack.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
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
        catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    private int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        layoutPlayerInventorySlots(playerInventory, leftCol, topRow);
    }

    protected void layoutPlayerInventorySlots(Inventory playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    public ItemStack findBag(Inventory playerInventory, Item item) {
        Player player = this.playerEntity;
        if (player.getMainHandItem().getItem() == item) {
            return player.getMainHandItem();
        }
        else if (player.getOffhandItem().getItem() == item) {
            return player.getOffhandItem();
        }
        else {
            for (int x = 0; x < playerInventory.getContainerSize(); x++) {
                ItemStack stack = playerInventory.getItem(x);
                if (stack.getItem() == item) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
