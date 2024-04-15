package com.lothrazar.cyclic.gui;

import com.lothrazar.cyclic.ModCyclic;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import com.lothrazar.library.capabilities.FluidTankBase;
import com.lothrazar.library.render.FluidRenderMap;
import com.lothrazar.cyclic.util.FabricHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import java.util.ArrayList;
import java.util.List;

public class FluidBar {
    public static final ResourceLocation FLUID_WIDGET = new ResourceLocation(ModCyclic.MODID, "textures/gui/fluid.png");
    public String emtpyTooltip = "0";
    private final Font font;
    private final int x;
    private final int y;
    private final int capacity;
    private int width = 18;
    private int height = 62;
    public int guiLeft;
    public int guiTop;

    public FluidBar(Font p, int cap) {
        this(p, 132, 8, cap);
    }

    public FluidBar(Font p, int x, int y, int cap) {
        font = p;
        this.x = x;
        this.y = y;
        this.capacity = cap;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void draw(GuiGraphics gg, FluidTankBase tank) {
        final int u = 0, v = 0, x = guiLeft + getX(), y = guiTop + getY();
        gg.blit(FLUID_WIDGET,
                x, y, u, v,
                width, height,
                width, height);
        //NOW the fluid part
        float capacity = tank.getCapacity();
        float amount = tank.getAmount();
        float scale = amount / capacity;
        int fluidAmount = (int) (scale * height);
        TextureAtlasSprite sprite = FluidRenderMap.getFluidTexture(tank.getFluid(), FluidRenderMap.FluidFlow.STILL);
        if (tank.getResource().getFluid() == Fluids.WATER) {
            //hack in the blue because water is grey and is filled in by the biome when in-world
            RenderSystem.setShaderColor(0, 0, 1, 1);
        }
        int xPosition = x + 1;
        int yPosition = y + 1;
        int maximum = height - 2;
        int desiredWidth = width - 2;
        int desiredHeight = fluidAmount - 2;
        //the .getBlitOffset() no longer exists.
        //good news we can drop vertexbuilder sprites and use gg blit this way
        //RenderUtils.drawTiledSprite(gg, xPosition,yPosition,yOffset, width - 2, fluidAmount - 2, sprite);
        if (sprite != null) {
            gg.blit(xPosition, yPosition + (maximum - desiredHeight), 0, desiredWidth, desiredHeight, sprite);
        }

        if (tank.getResource().getFluid() == Fluids.WATER) {
           RenderSystem.setShaderColor(1, 1, 1, 1); //un-apply the water filter
        }
    }

    public boolean isMouseover(int mouseX, int mouseY) {
        return guiLeft + x <= mouseX && mouseX <= guiLeft + x + width
                && guiTop + y <= mouseY && mouseY <= guiTop + y + height;
    }


    public void renderHoveredToolTip(GuiGraphics ms, int mouseX, int mouseY, FluidStack current) {
        if (this.isMouseover(mouseX, mouseY)) {
            this.renderTooltip(ms, mouseX, mouseY, current);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCapacity() {
        return capacity;
    }

    public void renderTooltip(GuiGraphics gg, int mouseX, int mouseY, FluidStack current) {
        String tt = emtpyTooltip;
        if (current.getAmount() > 0) {
            tt = FabricHelper.toMiliBuckets(current.getAmount()) + "/" + FabricHelper.toMiliBuckets(getCapacity())  + " " + current.getDisplayName().getString();
        }
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable(tt));
        gg.renderComponentTooltip(font, list, mouseX, mouseY);
    }
}
