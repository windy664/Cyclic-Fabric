package com.lothrazar.cyclic.block.anvilmagma;

import com.lothrazar.cyclic.block.BlockEntityCyclic;
import com.lothrazar.cyclic.porting.neoforge.items.ForgeImplementedInventory;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.library.util.ItemStackUtil;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import com.lothrazar.library.capabilities.FluidTankBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AnvilMagmaBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    enum Fields {
        TIMER, REDSTONE
    }

    public static final int CAPACITY = (int) (64 * FluidConstants.BUCKET);
    public static ForgeConfigSpec.IntValue FLUIDCOST;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public FluidTankBase tank = new FluidTankBase(this, CAPACITY, isFluidValid());

    public AnvilMagmaBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ANVIL_MAGMA.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, AnvilMagmaBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, AnvilMagmaBlockEntity e) {
        e.tick();
    }

    public void tick() {
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        setLitProperty(true);
        ItemStack stack = getItem(0);
        if (stack.isEmpty()) {
            //move it over and then done
            if (getItem(1).isEmpty()) {
                insertItem(1, stack.copy(), false);
                extractItem(0, stack.getCount(), false);
            }
            return;
        }
        final int repair = FLUIDCOST.get(); // fluid
        boolean work = false;
        if (tank != null &&
                tank.getAmount() >= repair &&
                stack.isDamageableItem() &&
                stack.getDamageValue() > 0) {
            //we can repair so steal some power
            //ok drain power
            work = true;
            try (Transaction transaction = Transaction.openOuter()) {
                tank.extract(tank.variant, repair, transaction);
                transaction.commit();
            }
        }
        //shift to other slot
        if (work) {
            ItemStackUtil.repairItem(stack);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory);
        tank.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        tank.amount = tag.getLong("amount");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("fluidVariant", tank.variant.toNbt());
        tag.putLong("amount", tank.amount);
        ContainerHelper.saveAllItems(tag, inventory);
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public int getField(int id) {
        switch (Fields.values()[id]) {
            case REDSTONE:
                return this.needsRedstone;
            case TIMER:
                return this.timer;
        }
        return 0;
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case TIMER:
                this.timer = value;
                break;
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.ANVIL_MAGMA.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new AnvilMagmaContainer(i, playerInventory, this, level, worldPosition);
    }

    public FluidTankBase getTank() {
        return tank;
    }

    public Predicate<FluidStack> isFluidValid() {
        return p -> {
            //Fluid fluid = p.getFluid();
            //return fluid == CyclicFluids.STILL_MAGMA;
            return true;
        };
    }

    @Override
    public void setFluid(FluidStack fluid) {
        tank.setFluid(fluid);
    }

    @Override
    public FluidStack getFluid() {
        return tank.getFluid();
    }
}
