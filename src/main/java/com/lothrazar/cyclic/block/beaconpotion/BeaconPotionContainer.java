package com.lothrazar.cyclic.block.beaconpotion;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.CyclicItems;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import java.util.List;

public class BeaconPotionContainer extends ContainerBase {
    BeaconPotionBlockEntity tile;
    private final Container inventory;

    public BeaconPotionContainer(int syncId, Inventory playerInventory, Level world, BlockPos pos) {
        this(syncId, playerInventory, new SimpleContainer(2), world, pos);
    }

    public BeaconPotionContainer(int syncId, Inventory playerInventory, Container inventorysent, Level world, BlockPos pos) {
        super(CyclicScreens.BEACON, syncId);
        tile = (BeaconPotionBlockEntity) world.getBlockEntity(pos);
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;
        this.inventory = inventorysent;
        this.endInv = 1;
        addSlot(new Slot(inventory, 1, 9, 35) {
            @Override
            public void setChanged() {
                tile.setChanged();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                List<MobEffectInstance> newEffects = PotionUtils.getMobEffects(stack);
                return newEffects.size() > 0;
            }
        });
        this.endInv++;
        addSlot(new Slot(inventory, 0, 149, 9) {
            @Override
            public void setChanged() {
                tile.setChanged();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == CyclicItems.ENTITY_DATA;
            }
        });
        layoutPlayerInventorySlots(playerInventory, 8, 84);
        this.trackAllIntFields(tile, BeaconPotionBlockEntity.Fields.values().length);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
