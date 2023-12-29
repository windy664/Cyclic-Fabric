package net.knsh.cyclic.library.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.knsh.cyclic.library.data.Model3D;
import net.knsh.cyclic.library.render.RenderResizableCuboid;

public class RenderBlockUtils {
    public static final int FULL_LIGHT = 0xF000F0;


    /**
     * used for fluid in-world render lighting
     *
     * @param
     * @param
     * @return
     */
    /*
    public static int calculateGlowLight(int light, FluidStack fluid) {
        return fluid.isEmpty() ? light
                : calculateGlowLight(light,
                fluid.getFluid().getFluidType().getLightLevel());
    }

    public static int calculateGlowLight(int light, int glow) {
        if (glow >= 15) {
            return FULL_LIGHT;
        }
        int blockLight = LightTexture.block(light);
        int skyLight = LightTexture.sky(light);
        return LightTexture.pack(Math.max(blockLight, glow), Math.max(skyLight, glow));
    }*/

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

    /*
    public static int getColorARGB(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return -1;
        }
        IClientFluidTypeExtensions fluidAttributes = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        return fluidAttributes.getTintColor(fluidStack);
    }*/

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
}
