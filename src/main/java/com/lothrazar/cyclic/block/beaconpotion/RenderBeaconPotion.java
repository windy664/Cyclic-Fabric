package com.lothrazar.cyclic.block.beaconpotion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderBeaconPotion implements BlockEntityRenderer<BeaconPotionBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public RenderBeaconPotion(BlockEntityRendererProvider.Context context) {}

    public void render(BeaconPotionBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!blockEntity.getBlockState().getValue(BeaconPotionBlock.LIT)) {
            return; // do not render if turned off
        }

        long l = blockEntity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = blockEntity.getBeamSections();
        int i = 0;

        for(int j = 0; j < list.size(); ++j) {
            BeaconBlockEntity.BeaconBeamSection beaconBeamSection = list.get(j);
            renderBeaconBeam(poseStack, buffer, partialTick, l, i, j == list.size() - 1 ? 1024 : beaconBeamSection.getHeight(), beaconBeamSection.getColor());
            i += beaconBeamSection.getHeight();
        }
    }

    public static void renderBeaconBeam(
            PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, long gameTime, int yOffset, int height, float[] colors
    ) {
        renderBeaconBeam(poseStack, bufferSource, BEAM_LOCATION, partialTick, 1.0F, gameTime, yOffset, height, colors, 0.2F, 0.25F);
    }

    public static void renderBeaconBeam(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            ResourceLocation beamLocation,
            float partialTick,
            float textureScale,
            long gameTime,
            int yOffset,
            int height,
            float[] colors,
            float beamRadius,
            float glowRadius
    ) {
        int i = yOffset + height;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float f = (float)Math.floorMod(gameTime, 40) + partialTick;
        float g = height < 0 ? f : -f;
        float h = Mth.frac(g * 0.2F - (float)Mth.floor(g * 0.1F));
        float j = colors[0];
        float k = colors[1];
        float l = colors[2];
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float m = 0.0F;
        float p = 0.0F;
        float q = -beamRadius;
        float r = 0.0F;
        float s = 0.0F;
        float t = -beamRadius;
        float u = 0.0F;
        float v = 1.0F;
        float w = -1.0F + h;
        float x = (float)height * textureScale * (0.5F / beamRadius) + w;
        renderPart(
                poseStack,
                bufferSource.getBuffer(RenderType.beaconBeam(beamLocation, false)),
                j,
                k,
                l,
                1.0F,
                yOffset,
                i,
                0.0F,
                beamRadius,
                beamRadius,
                0.0F,
                q,
                0.0F,
                0.0F,
                t,
                0.0F,
                1.0F,
                x,
                w
        );
        poseStack.popPose();
        m = -glowRadius;
        float n = -glowRadius;
        p = -glowRadius;
        q = -glowRadius;
        u = 0.0F;
        v = 1.0F;
        w = -1.0F + h;
        x = (float)height * textureScale + w;
        renderPart(
                poseStack,
                bufferSource.getBuffer(RenderType.beaconBeam(beamLocation, true)),
                j,
                k,
                l,
                0.125F,
                yOffset,
                i,
                m,
                n,
                glowRadius,
                p,
                q,
                glowRadius,
                glowRadius,
                glowRadius,
                0.0F,
                1.0F,
                x,
                w
        );
        poseStack.popPose();
    }

    private static void renderPart(
            PoseStack poseStack,
            VertexConsumer consumer,
            float red,
            float green,
            float blue,
            float alpha,
            int minY,
            int maxY,
            float x0,
            float z0,
            float x1,
            float z1,
            float x2,
            float z2,
            float x3,
            float z3,
            float minU,
            float maxU,
            float minV,
            float maxV
    ) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        renderQuad(matrix4f, matrix3f, consumer, red, green, blue, alpha, minY, maxY, x0, z0, x1, z1, minU, maxU, minV, maxV);
        renderQuad(matrix4f, matrix3f, consumer, red, green, blue, alpha, minY, maxY, x3, z3, x2, z2, minU, maxU, minV, maxV);
        renderQuad(matrix4f, matrix3f, consumer, red, green, blue, alpha, minY, maxY, x1, z1, x3, z3, minU, maxU, minV, maxV);
        renderQuad(matrix4f, matrix3f, consumer, red, green, blue, alpha, minY, maxY, x2, z2, x0, z0, minU, maxU, minV, maxV);
    }

    private static void renderQuad(
            Matrix4f pose,
            Matrix3f normal,
            VertexConsumer consumer,
            float red,
            float green,
            float blue,
            float alpha,
            int minY,
            int maxY,
            float minX,
            float minZ,
            float maxX,
            float maxZ,
            float minU,
            float maxU,
            float minV,
            float maxV
    ) {
        addVertex(pose, normal, consumer, red, green, blue, alpha, maxY, minX, minZ, maxU, minV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, minY, minX, minZ, maxU, maxV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, minY, maxX, maxZ, minU, maxV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, maxY, maxX, maxZ, minU, minV);
    }

    /**
     * @param u the left-most coordinate of the texture region
     * @param v the top-most coordinate of the texture region
     */
    private static void addVertex(
            Matrix4f pose, Matrix3f normal, VertexConsumer consumer, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v
    ) {
        consumer.vertex(pose, x, (float)y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public boolean shouldRenderOffScreen(BeaconPotionBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRender(BeaconPotionBlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(cameraPos.multiply(1.0, 0.0, 1.0), this.getViewDistance());
    }
}