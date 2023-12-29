package net.knsh.cyclic.item.crafting;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.item.ItemCyclic;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CraftingBagItem extends ItemCyclic implements ItemHandlerLookup {
    private final int slots = 9;
    private final ItemStackHandler inventory = new ItemStackHandler(slots) {
        @Override
        public boolean isItemValid(int slot, ItemVariant stack) {
            return !(stack.getItem() instanceof CraftingBagItem) && super.isItemValid(slot, stack);
        }
    };

    public CraftingBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && !player.isCrouching()) {
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            MenuProvider screenHandlerFactory = new CraftingBagContainerProvider(slot);
            player.openMenu(screenHandlerFactory);
        }
        return super.use(level, player, hand);
    }

    @Override
    public ItemStackHandler getItemHandler() {
        return inventory;
    }
}
