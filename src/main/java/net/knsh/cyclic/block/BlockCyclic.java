package net.knsh.cyclic.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.config.ClientConfigCyclic;
import net.knsh.flib.block.EntityBlockFlib;
import net.knsh.flib.ImplementedInventory;
import net.knsh.cyclic.lookups.Lookup;
import net.knsh.flib.util.SoundUtil;
import net.knsh.flib.util.StringParseUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class BlockCyclic extends EntityBlockFlib implements Lookup {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private boolean hasGui = false;
    private boolean hasFluidInteract = false;

    public BlockCyclic(Properties settings) {
        super(settings);
    }

    public static boolean never(BlockState bs, BlockGetter bg, BlockPos pos) {
        return false;
    }

    protected BlockCyclic setHasGui() {
        this.hasGui = true;
        return this;
    }

    protected BlockCyclic setHasFluidInteract() {
        this.hasFluidInteract = true;
        return this;
    }

    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return null;
    }

    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(BlockState state, @NotNull Rotation direction) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction oldDir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newDir = direction.rotate(oldDir);
            //still rotate on axis, if its valid
            if (newDir != Direction.UP && newDir != Direction.DOWN) {
                return state.setValue(BlockStateProperties.HORIZONTAL_FACING, newDir);
            }
        }
        if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction oldDir = state.getValue(BlockStateProperties.FACING);
            Direction newDir = direction.rotate(oldDir);
            // rotate state on axis dir
            return state.setValue(BlockStateProperties.FACING, newDir);
        }
        // default doesnt do much
        BlockState newState = state.rotate(direction);
        return newState;
    }

    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit
    ) {
        if (hasFluidInteract) {
            if (!world.isClientSide) {
                BlockEntity tankHere = world.getBlockEntity(pos);
                if (tankHere != null) {
                    Storage<FluidVariant> handler = FluidStorage.SIDED.find(world, pos, hit.getDirection());
                    if (handler != null) {
                        if (FluidStorageUtil.interactWithFluidStorage(handler, player, hand)) {
                            if (player instanceof ServerPlayer) {
                                SoundUtil.playSoundFromServer((ServerPlayer) player, SoundEvents.BUCKET_FILL, 1F, 1F);
                            }

                            if (StorageUtil.findStoredResource(handler, s -> true) != null) {
                                displayClientFluidMessage(player, handler);
                            }
                        } else {
                            displayClientFluidMessage(player, handler);
                        }
                    }
                }
            }
            if (ContainerItemContext.ofPlayerHand(player, hand).find(FluidStorage.ITEM) != null) {
                return InteractionResult.SUCCESS;
            }
        }
        if (this.hasGui) {
            if (!world.isClientSide) {
                MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

                if (screenHandlerFactory != null) {
                    player.openMenu(screenHandlerFactory);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    private void displayClientFluidMessage(Player player, Storage<FluidVariant> handler) {
        if (ClientConfigCyclic.FLUID_BLOCK_STATUS.get()) {
            player.displayClientMessage(Component.translatable(StringParseUtil.getFluidRatioName(handler)), true);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerClient() {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ImplementedInventory inventory) {
                for (int i = 0; i < inventory.getContainerSize(); ++i) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getItem(i));
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, movedByPiston);
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(id, param);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState bs, Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static boolean isItem(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        return hasCapabilityDir(facing, world, facingPos, ItemStorage.SIDED);
    }

    public static boolean isFluid(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        return hasCapabilityDir(facing, world, facingPos, FluidStorage.SIDED);
    }

    public static boolean isEnergy(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        return hasCapabilityDir(facing, world, facingPos, EnergyStorage.SIDED);
    }

    private static boolean hasCapabilityDir(Direction facing, LevelAccessor world, BlockPos facingPos, BlockApiLookup<?, @Nullable Direction> cap) {
        if (facing == null) {
            return false;
        }
        BlockEntity neighbor = world.getBlockEntity(facingPos);
        return neighbor != null && cap.find(neighbor.getLevel(), facingPos, facing.getOpposite()) != null;
    }

    @Override
    public void registerLookups() {}
}
