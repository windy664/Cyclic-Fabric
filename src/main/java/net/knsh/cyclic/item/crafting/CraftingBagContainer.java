package net.knsh.cyclic.item.crafting;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.data.IContainerCraftingAction;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.registry.CyclicItems;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CraftingBagContainer extends ContainerBase implements IContainerCraftingAction {
    private final TransientCraftingContainer craftMatrix = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer craftResult = new ResultContainer();

    public int slot = -1;
    public ItemStack bag;

    public CraftingBagContainer(int id, Inventory playerInventory, Player player, int slot) {
        super(CyclicScreens.CRAFTING_BAG, id);
        this.slot = slot;
        Cyclic.LOGGER.info("bag slot " + slot);
        this.playerEntity = player;
        this.playerInventory = playerInventory;
        this.endInv = 10;
        //result first
        this.addSlot(new ResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        if (slot > -1) {
            this.bag = playerInventory.getItem(slot);
            Cyclic.LOGGER.info("bag   " + bag);
        }
        if (bag.isEmpty()) {
            this.bag = super.findBag(playerInventory, CyclicItems.CRAFTING_BAG);
        }
        //grid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new Slot(craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18) {

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return !(stack.getItem() instanceof CraftingBagItem);
                    }
                });
            }
        }
        ItemHandlerLookup handler = CyclicItemLookup.ITEM_HANDLER.find(bag, null);

        if (handler != null) {
            SlottedStackStorage h = handler.getItemHandler();
            for (int j = 0; j < h.getSlotCount(); j++) {
                ItemStack inBag = h.getStackInSlot(j);
                if (!inBag.isEmpty()) {
                    this.craftMatrix.setItem(j, h.getStackInSlot(j));
                }
            }
        }
        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.craftResult.setItem(0, ItemStack.EMPTY);
        if (player.level().isClientSide == false) {
            ItemHandlerLookup parentHandler = CyclicItemLookup.ITEM_HANDLER.find(bag, null);
            if (parentHandler != null) {
                SlottedStackStorage handler = parentHandler.getItemHandler();
                for (int i = 0; i < 9; i++) {
                    ItemStack crafty = this.craftMatrix.getItem(i);
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (!handler.getStackInSlot(i).isEmpty()) handler.extractSlot(i, handler.getSlot(i).getResource(), 64, transaction);
                        if (!crafty.isEmpty()) handler.insertSlot(i, ItemVariant.of(crafty), crafty.getCount(), transaction);
                        transaction.commit();
                    }
                }
            }
        }
    }

    @Override
    public void slotsChanged(Container inventory) {
        Level world = playerEntity.level();
        if (!world.isClientSide) {
            ServerPlayer player = (ServerPlayer) playerEntity;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMatrix, world);
            if (optional.isPresent()) {
                CraftingRecipe icraftingrecipe = optional.get();
                if (craftResult.setRecipeUsed(world, player, icraftingrecipe)) {
                    itemstack = icraftingrecipe.assemble(craftMatrix, world.registryAccess());
                }
            }
            craftResult.setItem(0, itemstack);
            player.connection.send(new ClientboundContainerSetSlotPacket(containerId, this.getStateId(), 0, itemstack));
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (!(slotId < 0 || slotId >= this.slots.size())) {
            ItemStack myBag = this.slots.get(slotId).getItem();
            if (myBag.getItem() instanceof CraftingBagItem) {
                //lock the bag in place by returning empty
                return; // ItemStack.EMPTY;
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStack(Player playerIn, int index) {
        return super.quickMoveStack(playerIn, index);
    }

    @Override
    public CraftingContainer getCraftMatrix() {
        return this.craftMatrix;
    }

    @Override
    public ResultContainer getCraftResult() {
        return this.craftResult;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
