package net.knsh.flib.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.knsh.cyclic.util.FluidHelpers;
import net.knsh.flib.data.Model3D;
import net.knsh.flib.render.RenderResizableCuboid;
import net.knsh.flib.render.type.FakeBlockRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Map;

public class RenderBlockUtils {
    public static final int FULL_LIGHT = 0xF000F0;

    public static int calculateGlowLight(int light, FluidStack fluid) {
        return fluid.isEmpty() ? light
                : calculateGlowLight(light, 8);
    }

    public static int calculateGlowLight(int light, int glow) {
        if (glow >= 15) {
            return FULL_LIGHT;
        }
        int blockLight = LightTexture.block(light);
        int skyLight = LightTexture.sky(light);
        return LightTexture.pack(Math.max(blockLight, glow), Math.max(skyLight, glow));
    }

    public static int getColorARGB(FluidStack fluidStack, float fluidScale) {
        if (fluidStack.isEmpty()) {
            return -1;
        }
        return getColorARGB(fluidStack);
    }

    public static int getColorARGB(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return -1;
        }
        return FluidVariantRendering.getColor(fluidStack.getType());
    }

    public static void renderCube(Matrix4f matrix, VertexConsumer builder, BlockPos pos, Color color, float alpha) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;
        float startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;
        //down
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        //up
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        //east
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        //west
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        //south
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        //north
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
    }

    public static float getRed(int color) {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    public static float getGreen(int color) {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    public static float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static float getAlpha(int color) {
        return (color >> 24 & 0xFF) / 255.0F;
    }


    /**
     * Used for in-world fluid rendering Source reference from MIT open source https://github.com/mekanism/Mekanism/tree/1.15x
     * <p>
     * https://github.com/mekanism/Mekanism/blob/1.15x/LICENSE
     * <p>
     * See MekanismRenderer.
     **/
    public static void renderObject(Model3D object, PoseStack matrix, VertexConsumer buffer, int argb, int light) {
        if (object != null) {
            RenderResizableCuboid.INSTANCE.renderCube(object, matrix, buffer, argb, light);
        }
    }

    public static void renderColourCubes(PoseStack matrix, Vec3 view, Map<BlockPos, Color> coords, float scale, float alpha) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        final Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        matrix.pushPose();
        matrix.translate(-view.x(), -view.y(), -view.z());
        VertexConsumer builder = buffer.getBuffer(FakeBlockRenderTypes.TRANSPARENT_COLOUR);
        for (BlockPos posCurr : coords.keySet()) {
            matrix.pushPose();
            matrix.translate(posCurr.getX(), posCurr.getY(), posCurr.getZ());
            matrix.translate(-0.005f, -0.005f, -0.005f);
            matrix.scale(scale, scale, scale);
            matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
            RenderBlockUtils.renderCube(matrix.last().pose(), builder, posCurr, coords.get(posCurr), alpha);
            matrix.popPose();
        }
        matrix.popPose();
        RenderSystem.disableDepthTest();
        buffer.endBatch(FakeBlockRenderTypes.TRANSPARENT_COLOUR);
    }
}
