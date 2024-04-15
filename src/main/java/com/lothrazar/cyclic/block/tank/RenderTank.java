package com.lothrazar.cyclic.block.tank;

import com.lothrazar.cyclic.util.FluidHelpers;
import com.lothrazar.library.render.type.FluidTankRenderType;
import com.lothrazar.library.util.RenderBlockUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RenderTank implements BlockEntityRenderer<TileTank> {
    public RenderTank(BlockEntityRendererProvider.Context d) {}

    @Override
    public void render(TileTank tankHere, float v, PoseStack matrix,
                       MultiBufferSource renderer, int light, int overlayLight) {
        Storage<FluidVariant> handler = FluidStorage.SIDED.find(tankHere.getLevel(), tankHere.getBlockPos(), null);
        if (handler != null && handler instanceof SingleVariantStorage<FluidVariant> tank) {
            if (tank.getResource() == null) {
                return;
            }
            FluidStack fluid = new FluidStack(tank.variant, tank.amount);
            if (fluid.isEmpty()) {
                return;
            }
            VertexConsumer buffer = renderer.getBuffer(FluidTankRenderType.RESIZABLE);
            matrix.scale(1F, FluidHelpers.getScale(tankHere.tank), 1F);
            RenderBlockUtils.renderObject(FluidHelpers.getFluidModel(fluid, FluidHelpers.STAGES - 1),
                    matrix, buffer, RenderBlockUtils.getColorARGB(fluid),
                    RenderBlockUtils.calculateGlowLight(light, fluid));
        }
    }
}
