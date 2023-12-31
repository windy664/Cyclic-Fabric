package net.knsh.cyclic.block.anvilmagma;

import net.knsh.cyclic.gui.ButtonMachineField;
import net.knsh.cyclic.gui.FluidBar;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AnvilMagmaScreen extends ScreenBase<AnvilMagmaContainer> {
    private ButtonMachineField btnRedstone;
    private FluidBar fluid;

    public AnvilMagmaScreen(AnvilMagmaContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        fluid = new FluidBar(this.font, 152, 8, AnvilMagmaBlockEntity.CAPACITY);
        int x, y;
        fluid.guiLeft = leftPos;
        fluid.guiTop = topPos;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, AnvilMagmaBlockEntity.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        fluid.renderHoveredToolTip(ms, mouseX, mouseY, menu.tile.getFluid());
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
        btnRedstone.onValueUpdate(menu.tile);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        this.drawSlot(ms, 54, 34);
        this.drawSlotLarge(ms, 104, 30);
        fluid.draw(ms, menu.tile.getTank());
    }
}
