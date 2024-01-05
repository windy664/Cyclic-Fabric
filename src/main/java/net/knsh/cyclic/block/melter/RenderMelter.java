package net.knsh.cyclic.block.melter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.knsh.cyclic.library.render.type.FluidTankRenderType;
import net.knsh.cyclic.library.util.RenderBlockUtils;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.lookups.types.FluidLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.util.FluidHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderMelter implements BlockEntityRenderer<TileMelter> {
    public RenderMelter(BlockEntityRendererProvider.Context d) {}

    @Override
    public void render(TileMelter tankHere, float v, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlayLight) {
        ItemHandlerLookup lookup = CyclicLookup.ITEM_HANDLER_SIDED.find(tankHere.getLevel(), tankHere.getBlockPos(), Direction.UP);
        var level = tankHere.getLevel();
        if (lookup != null) {
            SlottedStackStorage itemHandler = lookup.getItemHandler();
            ItemStack stack = itemHandler.getStackInSlot(0);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.translate(0.5, 0.60, 0.5);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, 0x111111, 200, matrixStack, buffer, level, light);
                matrixStack.popPose();
            }
            stack = itemHandler.getStackInSlot(1);
            if (!stack.isEmpty()) {
                matrixStack.pushPose();
                matrixStack.translate(0.5, 0.10, 0.5);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, 0x999999, 0, matrixStack, buffer, level, light);
                matrixStack.popPose();
            }
        }
        FluidLookup lookupFluid = CyclicLookup.FLUID_HANDLER.find(tankHere.getLevel(), tankHere.getBlockPos(), null);
        if (lookupFluid == null || lookupFluid.getFluidTank().getFluid() == null) {
            return;
        }
        FluidTank handler = lookupFluid.getFluidTank();
        FluidStack fluid = handler.getFluid();
        if (fluid.isEmpty()) {
            return;
        }
        VertexConsumer vertexBuffer = buffer.getBuffer(FluidTankRenderType.RESIZABLE);
        matrixStack.pushPose();
        matrixStack.scale(1F, FluidHelpers.getScale(tankHere.tank), 1F);
        RenderBlockUtils.renderObject(FluidHelpers.getFluidModel(fluid, FluidHelpers.STAGES - 1),
                matrixStack, vertexBuffer, 10000,
                10000);
        matrixStack.popPose();
    }
}
