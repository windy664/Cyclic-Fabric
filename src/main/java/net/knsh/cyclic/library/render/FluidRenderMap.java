package net.knsh.cyclic.library.render;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;

public class FluidRenderMap<V> extends Object2ObjectOpenCustomHashMap<FluidVariant, V> {

    public enum FluidFlow {
        STILL, FLOWING
    }

    public FluidRenderMap() {
        super(FluidHashStrategy.INSTANCE);
    }

    public static TextureAtlasSprite getFluidTexture(FluidVariant tank, FluidFlow type) {
        Fluid fluid = tank.getFluid();
        ResourceLocation spriteLocation = new ResourceLocation("cyclic:block/fluid/xpjuice_still");

        if (type == FluidFlow.STILL) {
            //spriteLocation = fluidAttributes.getStillTexture(fluidStack);
        }
        else {
            //spriteLocation = fluidAttributes.getFlowingTexture(fluidStack);
        }
        return getSprite(spriteLocation);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    /**
     * Implements equals & hashCode that ignore FluidStack#amount
     */
    public static class FluidHashStrategy implements Hash.Strategy<FluidVariant> {

        public static FluidHashStrategy INSTANCE = new FluidHashStrategy();

        @Override
        public int hashCode(FluidVariant stack) {
            if (stack == null || stack.isBlank()) {
                return 0;
            }
            int code = 1;
            code = 31 * code + stack.getFluid().hashCode();
            if (stack.hasNbt()) {
                code = 31 * code + stack.getFluid().hashCode();
            }
            return code;
        }

        @Override
        public boolean equals(FluidVariant a, FluidVariant b) {
            return a == null ? b == null : b != null && a.equals(b);
        }
    }
}