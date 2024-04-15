package com.lothrazar.flib.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.lothrazar.flib.util.ChatUtil;
import com.lothrazar.flib.util.ItemStackUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockFlib extends Block {
    private static final int MAX_CONNECTED_UPDATE = 18;
    public static final EnumProperty<DyeColor> COLOUR = EnumProperty.create("color", DyeColor.class);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static class Settings {
        boolean tooltip = false;
        boolean rotateColour = false;
        boolean rotateColourConsume = false;
        boolean litWhenPowered;
        private boolean facingAttachment;

        public Settings rotateColour(boolean consume) {
            this.rotateColour = true;
            this.rotateColourConsume = consume;
            return this;
        }

        public Settings litWhenPowered() {
            this.litWhenPowered = true;
            return this;
        }

        public Settings facingAttachment() {
            this.facingAttachment = true;
            return this;
        }

        public Settings tooltip() {
            this.tooltip = true;
            return this;
        }

        public Settings noTooltip() {
            this.tooltip = false;
            return this;
        }

        public void tooltipApply(Block block, List<Component> tooltipList) {
            tooltipList.add(ChatUtil.ilang(block.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
        }
    }

    Settings me;

    public BlockFlib(Properties prop) {
        this(prop, new Settings());
    }

    public BlockFlib(Properties prop, Settings custom) {
        super(prop);
        this.me = custom;
        BlockState def = defaultBlockState();
        if (me.rotateColour) {
            def = def.setValue(COLOUR, DyeColor.WHITE);
        }
        if (me.litWhenPowered) {
            def = def.setValue(LIT, Boolean.FALSE);
        }
        this.registerDefaultState(def);
    }

    public static Boolean never(BlockState s, BlockGetter w, BlockPos pos, EntityType<?> t) {
        return (boolean) false;
    }

    public static boolean never(BlockState s, BlockGetter w, BlockPos pos) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(@NotNull BlockState bs, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (me.facingAttachment) {
            Direction dir = bs.getValue(BlockStateProperties.FACING);
            return Block.canSupportCenter(level, pos.relative(dir), dir.getOpposite());
            //          : FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, dir);
        }
        return super.canSurvive(bs, level, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(
            @NotNull BlockState bs, @NotNull Direction face, @NotNull BlockState bsOp, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos posOther
    ) {
        if (me.facingAttachment) {
            return !bs.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(bs, face, bsOp, level, pos, posOther);
        }
        return super.updateShape(bs, face, bsOp, level, pos, posOther);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext ctx) {
        if (me.litWhenPowered) {
            return this.defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
        }
        return this.defaultBlockState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos posOther, boolean flagIn) {
        if (me.litWhenPowered && !level.isClientSide) {
            boolean flag = state.getValue(LIT);
            if (flag != level.hasNeighborSignal(pos)) {
                if (flag) {
                    level.scheduleTick(pos, this, 4);
                }
                else {
                    level.setBlock(pos, state.cycle(LIT), 2);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (me.litWhenPowered && state.getValue(LIT) && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(LIT), 2);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getDirectSignal(@NotNull BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return super.getDirectSignal(blockState, blockAccess, pos, side);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit
    ) {
        //    ItemStack heldStack = player.getItemInHand(hand);
        return super.use(state, world, pos, player, hand, hit);
    }

    /*
    public void onRightClickBlock(RightClickBlock event, BlockState state) {
        if (me.rotateColour &&
                event.getItemStack().getItem() instanceof DyeItem newColor) {
            boolean doConnected = event.getEntity().isCrouching();
            rotateDye(state, event.getLevel(), event.getPos(), event.getEntity(), event.getItemStack(), newColor.getDyeColor(), doConnected);
        }
    }*/

    public void rotateDye(BlockState state, Level world, BlockPos pos, Player player, ItemStack heldStack, DyeColor newColour, boolean doConnected) {
        DyeColor oldColour = state.getValue(COLOUR);
        if (newColour != oldColour) {
            //new color is different, NOW update
            world.setBlockAndUpdate(pos, state.setValue(COLOUR, newColour));
            if (me.rotateColourConsume) {
                ItemStackUtil.shrink(player, heldStack);
            }
            if (doConnected) {
                this.setConnectedColour(world, pos, oldColour, newColour, 0);
            }
        }
    }

    public void setConnectedColour(Level world, BlockPos pos, DyeColor oldColour, DyeColor newColor, int rec) {
        if (rec > MAX_CONNECTED_UPDATE) {
            return;
        }
        for (Direction d : Direction.values()) {
            BlockPos offset = pos.relative(d);
            BlockState here = world.getBlockState(offset);
            if (here.getBlock() == this && oldColour == here.getValue(COLOUR)) {
                world.setBlockAndUpdate(offset, here.setValue(COLOUR, newColor));
                rec++;
                this.setConnectedColour(world, offset, oldColour, newColor, rec);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (me.tooltip) {
            me.tooltipApply(this, tooltip);
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
