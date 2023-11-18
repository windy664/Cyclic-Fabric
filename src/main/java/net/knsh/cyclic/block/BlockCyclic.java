package net.knsh.cyclic.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCyclic extends BaseEntityBlock implements EntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private boolean hasGui = false;
    private boolean hasTooltip = true;

    public BlockCyclic(Properties settings) {
        super(settings);
    }

    protected BlockCyclic setHasGui() {
        this.hasGui = true;
        return this;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity == null ? false : blockentity.triggerEvent(id, param);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState bs, Level level, BlockPos pos) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        if (hasTooltip) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
