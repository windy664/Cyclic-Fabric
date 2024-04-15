package com.lothrazar.cyclic.block.conveyor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ConveyorItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<ConveyorItemEntity> {
    private final ItemRenderer renderer;

    public ConveyorItemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.renderer = renderManager.getItemRenderer();
        this.shadowRadius = 0.0F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(ConveyorItemEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        ItemStack stack = entity.getItem();
        BakedModel model = this.renderer.getModel(stack, entity.level(), null, light);
        this.renderer.render(stack, ItemDisplayContext.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY, model);
        matrices.popPose();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(ConveyorItemEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(ConveyorItemEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
