package net.knsh.cyclic.block.crafter;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.data.PreviewOutlineType;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.porting.neoforge.items.ForgeImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrafterBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    static final int MAX = 64000;
    public static final int TIMER_FULL = 40;
    public static ForgeConfigSpec.IntValue POWERCONF;

    public static final int IO_NUM_ROWS = 5;
    public static final int IO_NUM_COLS = 2;
    public static final int GRID_NUM_ROWS = 3;
    public static final int GRID_NUM_COLS = 3;
    public static final int IO_SIZE = IO_NUM_ROWS * IO_NUM_COLS;
    public static final int GRID_SIZE = GRID_NUM_ROWS * GRID_NUM_COLS;
    public static final int PREVIEW_SLOT = IO_SIZE * 2 + GRID_SIZE;
    public static final int OUTPUT_SLOT_START = IO_SIZE + GRID_SIZE;
    public static final int OUTPUT_SLOT_STOP = OUTPUT_SLOT_START + IO_SIZE - 1;
    public static final int GRID_SLOT_START = IO_SIZE;
    public static final int GRID_SLOT_STOP = GRID_SLOT_START + GRID_SIZE - 1;
    public static final int INPUT = 0;
    public static final int OUTPUT = 10;
    public static final int PREVIEW = 20;

    private final SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        protected void onFinalCommit() {
            CrafterBlockEntity.this.setChanged();
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public boolean supportsInsertion() {
            return true;
        }
    };

    SimpleContainer grid = new SimpleContainer(9);

    //20 is the preview slot
    //10-19 is the right output column
    //0-9 is input left column
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(21, ItemStack.EMPTY);

    enum ItemHandlers {
        INPUT, OUTPUT, GRID, PREVIEW
    };

    enum Fields {
        TIMER, REDSTONE, RENDER;
    }

    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.CRAFTER.blockEntity(), pos, state);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index != PREVIEW_SLOT && (index < OUTPUT_SLOT_START || index > OUTPUT_SLOT_STOP)) {
            ForgeImplementedInventory.super.canPlaceItem(index, stack);
        }
        return false;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, CrafterBlockEntity tile) {
        tile.serverTick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, CrafterBlockEntity tile) {
        tile.serverTick();
    }

    public void serverTick() {
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        setLitProperty(true);
        if (this.level.isClientSide())
            return;
        if (energy.getCapacity() < POWERCONF.get() && POWERCONF.get() > 0)
            return;
        if (timer < 0)
            timer = 0;
        if (--timer > 0)
            return;
        Recipe<CraftingContainer> lastValidRecipe = findMatchingRecipe();
        if (lastValidRecipe == null) {
            this.timer = TIMER_FULL;
            setPreviewSlot(ItemStack.EMPTY);
        } else {
            ItemStack recipeOutput = lastValidRecipe.getResultItem(level.registryAccess()).copy();
            setPreviewSlot(recipeOutput);

            if (hasFreeSpace(OUTPUT, recipeOutput)) {
                if (doCraft(lastValidRecipe)) {
                    this.timer = TIMER_FULL;
                    try (Transaction transaction = Transaction.openOuter()) {
                        this.energy.extract(POWERCONF.get(), transaction);
                        transaction.commit();
                    }
                    depositOutput(recipeOutput, OUTPUT);
                    NonNullList<ItemStack> rem = lastValidRecipe.getRemainingItems(craftMatrix);
                    for (int i = 0; i < rem.size(); ++i) {
                        ItemStack s = rem.get(i);
                        if (!s.isEmpty() && s.getItem() == craftMatrix.getItem(i).getItem()) {
                            s = depositOutput(s, INPUT);
                        }
                        depositOutput(s, OUTPUT);
                    }
                }
            }
        }
    }

    private ItemStack depositOutput(ItemStack recipeOutput, int dest) {
        if (recipeOutput.isEmpty()) {
            return recipeOutput;
        }
        for (int slotId = dest; slotId < 10 + dest; slotId++) {
            recipeOutput = this.insertItem(slotId, recipeOutput, false);
            if (recipeOutput.isEmpty()) {
                break;
            }
        }
        return recipeOutput;
    }

    private void setPreviewSlot(ItemStack itemStack) {
        setItem(PREVIEW, itemStack);
    }

    private boolean hasFreeSpace(int inv, ItemStack output) {
        ItemStack test = output.copy();
        for (int slotId = inv; slotId < 10 + inv; slotId++) {
            test = this.insertItem(slotId, test, true);
        }
        return test.isEmpty();
    }

    private boolean doCraft(Recipe<CraftingContainer> lastValidRecipe) {
        HashMap<Integer, List<ItemStack>> putbackStacks = new HashMap<>();
        for (Ingredient ingredient : lastValidRecipe.getIngredients()) {
            if (ingredient.isEmpty())
                continue;
            boolean matched = false;
            for (int index = 0; index < 10; index++) {
                ItemStack itemStack = getItem(index);
                if (ingredient.test(itemStack)) {
                    if (putbackStacks.containsKey(index)) {
                        putbackStacks.get(index).add(getItem(index).copy());
                    } else {
                        List<ItemStack> list = new ArrayList<>();
                        list.add(getItem(index).copy());
                        putbackStacks.put(index, list);
                    }
                    matched = true;
                    this.extractItem(index, 1, false);
                    break;
                }
            }
            if (!matched) {
                putbackStacks(putbackStacks, INPUT);
                return false;
            }
        }
        return true;
    }

    private void putbackStacks(HashMap<Integer, List<ItemStack>> putbackStacks, int io) {
        for (HashMap.Entry<Integer, List<ItemStack>> entry : putbackStacks.entrySet()) {
            for (ItemStack stack : entry.getValue()) {
                this.insertItem(entry.getKey(), stack, false);
            }
        }
    }

    private Recipe<CraftingContainer> findMatchingRecipe() {
        for (int i = 0; i < 9; i++) {
            craftMatrix.setItem(i, grid.getItem(i).copy());
        }
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        for (CraftingRecipe rec : recipes) {
            if (rec.matches(craftMatrix, level)) {
                return rec;
            }
        }
        return null;
    }

    public static class FakeContainer extends AbstractContainerMenu {

        protected FakeContainer(MenuType<?> type, int id) {
            super(type, id);
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return true;
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }
    }

    private final TransientCraftingContainer craftMatrix = new TransientCraftingContainer(new FakeContainer(MenuType.CRAFTING, 18291238), 3, 3);

    //public SimpleEnergyStorage getCrafterEnergy() {
    //    return energy;
    //}

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory);
        ContainerHelper.loadAllItems(tag, grid.items);
        energy.amount = tag.getLong("amount");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory);
        ContainerHelper.saveAllItems(tag, grid.items);
        tag.putLong("amount", energy.amount);
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setField(int id, int value) {
        switch (CrafterBlockEntity.Fields.values()[id]) {
            case TIMER:
                this.timer = value;
                break;
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case RENDER:
                this.render = value % PreviewOutlineType.values().length;
                break;
        }
    }

    @Override
    public int getField(int id) {
        return switch (Fields.values()[id]) {
            case TIMER -> timer;
            case REDSTONE -> this.needsRedstone;
            case RENDER -> render;
        };
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.CRAFTER.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrafterContainer(i, inventory, this, level, worldPosition);
    }
}
