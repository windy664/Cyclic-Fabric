package com.lothrazar.cyclic.block.anvilvoid;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AnvilVoidContainer extends ContainerBase {
    AnvilVoidBlockEntity tile;
    private final Container inventory;

    public AnvilVoidContainer(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(2), world, pos);
    }

    public AnvilVoidContainer(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.ANVIL_VOID, syncId);

        ModCyclic.LOGGER.info(String.valueOf(inventorysent.getContainerSize()) + world.isClientSide());
        tile = (AnvilVoidBlockEntity) world.getBlockEntity(pos);
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;
        this.inventory = inventorysent;

        this.addSlot(new Slot(inventory, 0, 55, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK || (stack.getTag() != null && stack.getTag().contains("Enchantments"));
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
            }
        });
        this.addSlot(new Slot(inventory, 1, 109, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void setByPlayer(ItemStack stack) {
                super.setByPlayer(stack);
            }
        });

        this.endInv = 2;
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields(tile, AnvilVoidBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
