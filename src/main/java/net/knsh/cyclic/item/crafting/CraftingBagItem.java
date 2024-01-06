package net.knsh.cyclic.item.crafting;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.lookups.Lookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.item.ItemCyclic;
import net.knsh.cyclic.registry.CyclicItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CraftingBagItem extends ItemCyclic implements Lookup {
    private final int slots = 9;

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
    public void registerLookups() {
        ItemStackHandler inventory = new ItemStackHandler(slots) {
            @Override
            public boolean isItemValid(int slot, ItemVariant stack) {
                return !(stack.getItem() instanceof CraftingBagItem) && super.isItemValid(slot, stack);
            }
        };

        CyclicItemLookup.ITEM_HANDLER.registerForItems(((itemStack, context) -> getInventoryFromTag(itemStack, inventory)), CyclicItems.CRAFTING_BAG);
    }
}
