package net.knsh.cyclic.gui;

import net.knsh.cyclic.api.IHasTooltip;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class ButtonTextured extends Button implements IHasTooltip {
    private TextureEnum textureId;
    private List<Component> tooltips;
    public int xOffset = 0;
    public int yOffset = 0;

    public ButtonTextured(int xPos, int yPos, int width, int height, String displayString, OnPress handler) {
        super(xPos, yPos, width, height, Component.translatable(displayString), handler, textSupplier -> textSupplier.get());
    }

    public ButtonTextured(int xPos, int yPos, int width, int height, TextureEnum tid, String tooltip, OnPress handler) {
        super(xPos, yPos, width, height, Component.translatable(""), handler, textSupplier -> textSupplier.get());
        this.setTooltip(tooltip);
        this.setTextureId(tid);
    }

    public void setTextureId(TextureEnum id) {
        this.textureId = id;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (textureId != null) {
            context.blit(CyclicTextures.WIDGETS,
                    this.getX() + textureId.getOffsetX(), this.getY() + textureId.getOffsetY(),
                    textureId.getX() + xOffset, textureId.getY() + yOffset,
                    textureId.getWidth() - yOffset, textureId.getHeight() - yOffset);
        }
    }

    @Override
    public List<Component> getTooltips() {
        return tooltips;
    }

    @Override
    public void setTooltip(String tooltip) {
        tooltips = new ArrayList<>();
        addTooltip(tooltip);
    }

    @Override
    public void addTooltip(String tooltip) {
        tooltips.add(Component.translatable(tooltip));
    }
}
