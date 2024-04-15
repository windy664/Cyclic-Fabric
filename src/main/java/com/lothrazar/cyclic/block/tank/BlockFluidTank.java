package com.lothrazar.cyclic.block.tank;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicItems;
import com.lothrazar.flib.util.ItemStackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockFluidTank extends BlockCyclic {
    public static final BooleanProperty TANK_ABOVE = BooleanProperty.create("above");
    public static final BooleanProperty TANK_BELOW = BooleanProperty.create("below");

    public BlockFluidTank(Properties properties) {
        super(properties.strength(1.2F).noOcclusion());
        this.setHasFluidInteract();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isCrouching() && player.getItemInHand(hand).getItem() == this.asItem()
                && (hit.getDirection() == Direction.UP || hit.getDirection() == Direction.DOWN)) {
            //pass to allow quick building up and down
            return InteractionResult.PASS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TANK_ABOVE, TANK_BELOW);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        boolean tileAbove = world.getBlockEntity(pos.above()) instanceof TileTank;
        boolean tileBelow = world.getBlockEntity(pos.below()) instanceof TileTank;
        return state
                .setValue(TANK_ABOVE, tileAbove)
                .setValue(TANK_BELOW, tileBelow);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileTank(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.TANK.blockEntity(), world.isClientSide ? TileTank::clientTick : TileTank::serverTick);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return new ArrayList<>();
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        try {
            ContainerItemContext ctx = ContainerItemContext.withConstant(stack);
            Storage<FluidVariant> storage = ctx.find(FluidStorage.ITEM);
            FluidVariant storedResource = StorageUtil.findStoredResource(storage);
            BlockEntity container = world.getBlockEntity(pos);
            if (storage != null && container != null && storedResource != null) {
                Storage<FluidVariant> storageTile = FluidStorage.SIDED.find(world, pos, container.getBlockState(), container, null);
                if (storageTile != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        storageTile.insert(storedResource, FluidConstants.BLOCK, transaction);
                        transaction.commit();
                    }
                }
            }
        }
        catch (Exception e) {
            Cyclic.LOGGER.error("Error during fill from item ", e);
        }
        //set default state
        state = state.setValue(TANK_ABOVE, false).setValue(TANK_BELOW, false);
        world.setBlockAndUpdate(pos, state);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity ent, ItemStack stackTool) {
        super.playerDestroy(world, player, pos, state, ent, stackTool);
        ItemStack stack = new ItemStack(this);
        Cyclic.LOGGER.info(stack.getDescriptionId());
        ContainerItemContext ctx = ContainerItemContext.withConstant(stack);
        if (ent != null) {
            Storage<FluidVariant> fluidInStack = ctx.find(FluidStorage.ITEM);
            if (fluidInStack != null && ent instanceof TileTank && fluidInStack instanceof SingleVariantStorage<FluidVariant>) {
                // push fluid from dying tank to itemstack
                TileTank ttank = (TileTank) ent;
                SingleVariantStorage<FluidVariant> storage = ItemBlockTank.getFluidStorage(stack, ctx);
                ItemBlockTank.readTank(ttank.getUpdateTag(), storage);
                stack.getOrCreateTag().put("BlockEntityTag", ItemBlockTank.writeTank(new CompoundTag(), storage));
            }
        }
        ItemStackUtil.dropItemStackMotionless(world, pos, stack);
    }

    @Override
    public void registerLookups() {
        FluidStorage.ITEM.registerForItems(ItemBlockTank::getFluidStorage, CyclicItems.TANK);
        FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.tank), CyclicBlocks.TANK.blockEntity());
    }
}
