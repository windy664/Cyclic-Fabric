package com.lothrazar.cyclic.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.lothrazar.cyclic.util.IHasTooltip;
import com.lothrazar.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ScreenBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public ScreenBase(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    protected void drawBackground(GuiGraphics ms, ResourceLocation gui) {
        //    RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //    RenderSystem.setShaderTexture(0, gui);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        ms.blit(gui, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    /*
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element btn : this.children()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            int mouseX = (int) (mc.mouse.getX() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
            int mouseY = (int) (mc.mouse.getY() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
            if (btn instanceof GuiSliderInteger && btn.isMouseOver(mouseX, mouseY)) {
                return btn.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        for (GuiEventListener widget : this.children()) {
            if (widget instanceof TextBoxAutosave) {
                //without this, txt boxes still work BUT:
                //keybindings like E OPEN INVENTORY dont make trigger the textbox, oops
                TextBoxAutosave txt = (TextBoxAutosave) widget;
                if (txt.isFocused()) {
                    return txt.keyPressed(keyCode, scanCode, modifiers);
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }*/

    protected void drawSlot(GuiGraphics ms, int x, int y, ResourceLocation texture) {
        drawSlot(ms, x, y, texture, 18);
    }

    protected void drawSlot(GuiGraphics ms, int x2, int y2, ResourceLocation texture, int size) {
        //    this.minecraft.getTextureManager().bind(texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        ms.blit(texture, leftPos + x2, topPos + y2, 0, 0, size, size, size, size);
    }

    protected void drawSlot(GuiGraphics ms, int x, int y) {
        drawSlot(ms, x, y, CyclicTextures.SLOT, 18);
    }

    protected void drawSlotLarge(GuiGraphics ms, int x, int y) {
        drawSlot(ms, x, y, CyclicTextures.SLOT_LARGE, 26);
    }

    protected void drawName(GuiGraphics ms, String name) {
        drawString(ms, name, (float) (this.imageWidth - this.font.width(name)) / 2, 6.0F);
    }

    protected void drawString(GuiGraphics gg, String name, float x, float y) {
        gg.drawString(font, name, (int) x, (int) y, 4210752, false);
    }

    public void drawButtonTooltips(GuiGraphics gg, int mouseX, int mouseY) {
        for (GuiEventListener btn : this.children()) {
            if (btn instanceof IHasTooltip ww && btn.isMouseOver(mouseX, mouseY)) {
                if (ww.getTooltips() != null) {
                    gg.renderComponentTooltip(font, ww.getTooltips(), mouseX - leftPos, mouseY - topPos);
                }
            }
        }
        for (GuiEventListener widget : this.children()) {
            if (widget instanceof IHasTooltip txt && widget.isMouseOver(mouseX, mouseY)) {
                if (txt.getTooltips() != null) {
                    gg.renderComponentTooltip(font, txt.getTooltips(), mouseX - leftPos, mouseY - topPos);
                }
            }
        }
    }
}
