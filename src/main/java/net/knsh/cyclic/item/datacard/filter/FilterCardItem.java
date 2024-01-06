package net.knsh.cyclic.item.datacard.filter;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.ItemStackUtil;
import io.github.fabricators_of_create.porting_lib.util.NetworkHooks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.knsh.cyclic.item.ItemCyclic;
import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.lookups.Lookup;
import net.knsh.cyclic.registry.CyclicItems;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FilterCardItem extends ItemCyclic implements Lookup {
    public static final int SLOT_FLUID = 8;
    private static final String NBTFILTER = "filter";

    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected int getStackLimit(int slot, ItemVariant resource) {
            return 1;
        }
    };

    public FilterCardItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            boolean isIgnore = getIsIgnoreList(stack);
            MutableComponent t = Component.translatable("cyclic.screen.filter." + isIgnore);
            t.withStyle(isIgnore ? ChatFormatting.DARK_GRAY : ChatFormatting.DARK_BLUE);
            tooltip.add(t);
            // caps arent synced from server very well
            //
            CompoundTag stackTag = stack.getOrCreateTag();
            if (stackTag.contains("fluidTooltip")) {
                String fluidTooltip = stackTag.getString("fluidTooltip");
                tooltip.add(Component.translatable(fluidTooltip).withStyle(ChatFormatting.AQUA));
            }
            if (stackTag.contains("itemCount")) {
                int itemCount = stackTag.getInt("itemCount");
                if (itemCount > 0) {
                    if (stackTag.contains("itemTooltip")) {
                        String itemTooltip = stackTag.getString("itemTooltip");
                        tooltip.add(Component.translatable(itemTooltip).withStyle(ChatFormatting.GRAY));
                    }
                    tooltip.add(Component.translatable("cyclic.screen.filter.item.count").append("" + itemCount).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else {
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide && !playerIn.isCrouching()) {
            NetworkHooks.openScreen((ServerPlayer) playerIn, new ContainerProviderFilterCard(), playerIn.blockPosition());
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void registerClient() {
        MenuScreens.register(CyclicScreens.FILTER_DATA, ScreenFilterCard::new);
    }

    public static void toggleFilterType(ItemStack filter) {
        boolean prev = getIsIgnoreList(filter);
        filter.getTag().putBoolean(NBTFILTER, !prev);
    }

    public static FluidStack getFluidStack(ItemStack filterStack) {
        if (filterStack.getItem() instanceof FilterCardItem == false) {
            return FluidStack.EMPTY; //filter is air, everything allowed
        }
        SlottedStackStorage myFilter = CyclicItemLookup.ITEM_HANDLER.find(filterStack, null);
        if (myFilter != null) {
            ItemStack bucket = myFilter.getStackInSlot(SLOT_FLUID);
            Storage<FluidVariant> fluidInStack = FluidStorage.ITEM.find(bucket, null);
            if (fluidInStack != null) {
                ResourceAmount<FluidVariant> storedResource = StorageUtil.findExtractableContent(fluidInStack, null);
                if (storedResource != null) {
                    return new FluidStack(storedResource);
                }
            }
        }
        return FluidStack.EMPTY;
    }

    public static boolean filterAllowsExtract(ItemStack filterStack, ItemStack itemTarget) {
        if (filterStack.getItem() instanceof FilterCardItem == false) {
            return true; //filter is air, everything allowed
        }
        //does my filter allow extract
        boolean isEmpty = false;
        boolean isMatchingList = false;
        boolean isIgnoreList = getIsIgnoreList(filterStack);
        SlottedStackStorage myFilter = CyclicItemLookup.ITEM_HANDLER.find(filterStack, null);
        if (myFilter != null) {
            for (int i = 0; i < myFilter.getSlotCount(); i++) {
                ItemStack filterPtr = myFilter.getStackInSlot(i);
                if (!filterPtr.isEmpty()) {
                    isEmpty = false; //at least one thing is in the filter
                    //does it match
                    if (ItemStackUtil.areTagsEqual(itemTarget, filterPtr)) {
                        isMatchingList = true;
                        break;
                    }
                }
            }
        }
        if (isIgnoreList) {
            // we are allowed to filter if it doesnt match
            return !isMatchingList;
        }
        else {
            //its an Allow list. filter if in the list
            //but if its empty, allow just lets everything
            return isEmpty || isMatchingList;
        }
    }

    private static boolean getIsIgnoreList(ItemStack filterStack) {
        return filterStack.getOrCreateTag().getBoolean(NBTFILTER);
    }

    public static boolean filterAllowsExtract(ItemStack filterStack, FluidStack fluidInTank) {
        if (filterStack.getItem() instanceof FilterCardItem == false) {
            return true; //filter is air, everything allowed
        }
        FluidStack fluidFilter = getFluidStack(filterStack);
        boolean isMatchingList = fluidFilter.getFluid() == fluidInTank.getFluid();
        boolean isIgnoreList = getIsIgnoreList(filterStack);
        //
        if (isIgnoreList) {
            return !isMatchingList;
        }
        else { // allow list
            return fluidFilter.isEmpty() || isMatchingList;
        }
    }

    @Override
    public void registerLookups() {
        // allows for external classes to access the filters inventory
        CyclicItemLookup.ITEM_HANDLER.registerForItems(((itemStack, context) -> getInventoryFromTag(itemStack, inventory)), CyclicItems.FILTER_DATA);
    }

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        FluidStack fluidStack = FilterCardItem.getFluidStack(stack);
        if (!fluidStack.isEmpty()) {
            nbt.putString("fluidTooltip", fluidStack.getDisplayName().getString());
        }
        SlottedStackStorage cap = CyclicItemLookup.ITEM_HANDLER.find(stack, null);
        //on server  this runs . also has correct values.
        //set data for sync to client
        if (cap != null) {
            int count = 0;
            Component first = null;
            for (int i = 0; i < cap.getSlotCount(); i++) {
                if (!cap.getStackInSlot(i).isEmpty()) {
                    //non empty stack eh
                    count++;
                    if (first == null) {
                        first = cap.getStackInSlot(i).getHoverName();
                    }
                }
            }
            nbt.putInt("itemCount", count);
            if (first != null) {
                nbt.putString("itemTooltip", first.getString());
            }
        }
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        if (nbt != null) {
            CompoundTag stackTag = stack.getOrCreateTag();
            stackTag.putString("itemTooltip", nbt.getString("itemTooltip"));
            stackTag.putString("fluidTooltip", nbt.getString("fluidTooltip"));
            stackTag.putInt("itemCount", nbt.getInt("itemCount"));
        }
        super.readShareTag(stack, nbt);
    }
}
