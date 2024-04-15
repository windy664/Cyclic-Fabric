package com.lothrazar.cyclic.block.wireless.fluid;

import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicScreens;
import com.lothrazar.flib.block.EntityBlockFlib;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWirelessFluid extends BlockCyclic {
    private static final double BOUNDS = 4;
    public static final VoxelShape AABB = Block.box(BOUNDS, BOUNDS, BOUNDS, 16 - BOUNDS, 16 - BOUNDS, 16 - BOUNDS);

    public BlockWirelessFluid(BlockBehaviour.Properties properties) {
        super(properties.strength(1.2F).noOcclusion());
        this.setHasGui();
        this.registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public void registerClient() {
        MenuScreens.register(CyclicScreens.WIRELESS_FLUID, ScreenWirelessFluid::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileWirelessFluid(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return EntityBlockFlib.createTickerHelper(type, CyclicBlocks.WIRELESS_FLUID.blockEntity(), world.isClientSide ? TileWirelessFluid::clientTick : TileWirelessFluid::serverTick);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileWirelessFluid tileentity = (TileWirelessFluid) worldIn.getBlockEntity(pos);
            if (tileentity != null && tileentity.gpsSlots != null) {
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.gpsSlots.getStackInSlot(0));
            }
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void registerLookups() {
        FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.tank), CyclicBlocks.WIRELESS_FLUID.blockEntity());
    }
}
