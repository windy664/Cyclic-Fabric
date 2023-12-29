package net.knsh.cyclic.item.crafting.simple;

import net.knsh.cyclic.item.ItemCyclic;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CraftingStickItem extends ItemCyclic {
    public CraftingStickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide && !playerIn.isCrouching()) {
            int slot = handIn == InteractionHand.MAIN_HAND ? playerIn.getInventory().selected : 40;
            MenuProvider screenHandlerFactory = new CraftingStickContainerProvider(slot);
            playerIn.openMenu(screenHandlerFactory);
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
