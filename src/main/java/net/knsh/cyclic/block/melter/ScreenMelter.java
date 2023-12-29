package net.knsh.cyclic.block.melter;

import net.knsh.cyclic.gui.EnergyBar;
import net.knsh.cyclic.gui.FluidBar;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.gui.TexturedProgress;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMelter extends ScreenBase<ContainerMelter> {
    private EnergyBar energy;
    private FluidBar fluid;
    private TexturedProgress progress;

    public ScreenMelter(ContainerMelter screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        energy = new EnergyBar(this.font, TileMelter.MAX);
        fluid = new FluidBar(this.font, 132, 8, TileMelter.CAPACITY);
        this.progress = new TexturedProgress(this.font, 68, 37, 24, 17, CyclicTextures.ARROW);
        this.progress.setTopDown(false);
        progress.guiLeft = fluid.guiLeft = energy.guiLeft = leftPos;
        progress.guiTop = fluid.guiTop = energy.guiTop = topPos;
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, (int) menu.tile.energy.getAmount());
        fluid.renderHoveredToolTip(ms, mouseX, mouseY, menu.tile.tank);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, title.getString());
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        energy.draw(ms, menu.tile.energy.getAmount());
        this.progress.max = menu.tile.getField(TileMelter.Fields.BURNMAX.ordinal());
        progress.draw(ms, menu.tile.getField(TileMelter.Fields.TIMER.ordinal()));
        fluid.draw(ms, menu.tile.tank);
        this.drawSlot(ms, 16, 30);
        this.drawSlot(ms, 34, 30);
    }
}
