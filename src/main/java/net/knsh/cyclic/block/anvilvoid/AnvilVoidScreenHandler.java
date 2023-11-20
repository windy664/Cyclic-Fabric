package net.knsh.cyclic.block.anvilvoid;

import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.gui.ScreenHandlerBase;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AnvilVoidScreenHandler extends ScreenHandlerBase {
    AnvilVoidBlockEntity tile;
    private final Container inventory;

    public AnvilVoidScreenHandler(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(2), world, pos);
    }

    public AnvilVoidScreenHandler(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.ANVIL_VOID, syncId);

        Cyclic.LOGGER.info(String.valueOf(inventorysent.getContainerSize()) + world.isClientSide());
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
