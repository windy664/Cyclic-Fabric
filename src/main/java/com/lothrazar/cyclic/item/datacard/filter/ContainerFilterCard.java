package com.lothrazar.cyclic.item.datacard.filter;

import com.lothrazar.cyclic.gui.ContainerBase;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import com.lothrazar.library.core.Const;
import com.lothrazar.cyclic.registry.CyclicItems;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerFilterCard extends ContainerBase {
    public ItemStack bag;
    public int slot;
    public int slotcount;

    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected int getStackLimit(int slot, ItemVariant resource) {
            return 1;
        }
    };

    public ContainerFilterCard(int id, Inventory playerInventory, Player player) {
        super(CyclicScreens.FILTER_DATA, id);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = 9;
        if (player.getMainHandItem().getItem() instanceof FilterCardItem) {
            this.bag = player.getMainHandItem();
            this.slot = player.getInventory().selected;
        }
        else if (player.getOffhandItem().getItem() instanceof FilterCardItem) {
            this.bag = player.getOffhandItem();
            this.slot = 40;
        }

        CompoundTag nbt = bag.getTag();

        if (nbt != null) {
            this.inventory.deserializeNBT(nbt);
        }

        this.slotcount = inventory.getSlotCount();
        for (int j = 0; j < inventory.getSlotCount(); j++) {
            int row = j / 9;
            int col = j % 9;
            int xPos = 8 + col * Const.SQ;
            int yPos = 32 + row * Const.SQ;
            this.addSlot(new SlotItemHandler(inventory, j, xPos, yPos) {
                @Override
                public void setChanged() {
                    bag.setTag(inventory.serializeNBT());
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    if (stack.getItem() == CyclicItems.FILTER_DATA) {
                        return false;
                    }
                    return super.mayPlace(stack);
                }
            });
        }
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        bag.setTag(inventory.serializeNBT());
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (!(slotId < 0 || slotId >= this.slots.size())) {
            ItemStack myBag = this.slots.get(slotId).getItem();
            if (myBag.getItem() instanceof FilterCardItem) {
                //lock the bag in place by returning empty
                return; //ItemStack.EMPTY;
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }
}
