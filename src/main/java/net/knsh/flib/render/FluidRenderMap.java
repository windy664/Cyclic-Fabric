package net.knsh.flib.render;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.knsh.cyclic.util.FluidHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public class FluidRenderMap<V> extends Object2ObjectOpenCustomHashMap<FluidStack, V> {

    public enum FluidFlow {
        STILL, FLOWING
    }

    public FluidRenderMap() {
        super(FluidHashStrategy.INSTANCE);
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack, FluidFlow type) {
        return FluidVariantRendering.getSprite(fluidStack.getType());
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    /**
     * Implements equals & hashCode that ignore FluidStack#amount
     */
    public static class FluidHashStrategy implements Hash.Strategy<FluidStack> {

        public static FluidHashStrategy INSTANCE = new FluidHashStrategy();

        @Override
        public int hashCode(FluidStack stack) {
            if (stack == null || stack.isEmpty()) {
                return 0;
            }
            int code = 1;
            code = 31 * code + stack.getFluid().hashCode();
            if (stack.hasTag()) {
                code = 31 * code + stack.getFluid().hashCode();
            }
            return code;
        }

        @Override
        public boolean equals(FluidStack a, FluidStack b) {
            return Objects.equals(a, b);
        }
    }
}