package net.knsh.cyclic.block.crafter;

import net.knsh.cyclic.gui.ButtonMachineField;
import net.knsh.cyclic.gui.EnergyBar;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.gui.TexturedProgress;
import net.knsh.cyclic.library.core.Const;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen extends ScreenBase<CrafterScreenHandler> {
    private EnergyBar energy;
    private ButtonMachineField btnRedstone;
    private TexturedProgress progress;

    public CrafterScreen(CrafterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageHeight = 256;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyBar(this.font, CrafterBlockEntity.MAX);
        this.energy.setHeight(120);
        this.progress = new TexturedProgress(this.font, CrafterScreenHandler.PREVIEW_START_X - 3, CrafterScreenHandler.PREVIEW_START_Y + Const.SQ, 24, 17, CyclicTextures.ARROW);
        this.progress.max = CrafterBlockEntity.TIMER_FULL;
        this.progress.setTopDown(false);
        int x, y;
        energy.guiLeft = progress.guiLeft = leftPos;
        energy.guiTop = progress.guiTop = topPos;
        energy.visible = CrafterBlockEntity.POWERCONF.get() > 0;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, CrafterBlockEntity.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, (int) menu.tile.getEnergy().amount);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        btnRedstone.onValueUpdate(menu.tile);
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY_LARGE_PLAIN);
        energy.draw(ms, menu.tile.getEnergy().amount);
        for (int rowPos = 0; rowPos < CrafterBlockEntity.IO_NUM_ROWS; rowPos++) {
            for (int colPos = 0; colPos < CrafterBlockEntity.IO_NUM_COLS; colPos++) {
                this.drawSlot(ms, CrafterScreenHandler.INPUT_START_X - 1 + colPos * Const.SQ,
                        CrafterScreenHandler.INPUT_START_Y - 1 + rowPos * Const.SQ);
                this.drawSlot(ms, CrafterScreenHandler.OUTPUT_START_X - 1 + colPos * Const.SQ,
                        CrafterScreenHandler.OUTPUT_START_Y - 1 + rowPos * Const.SQ);
            }
        }
        for (int colPos = 0; colPos < CrafterBlockEntity.GRID_NUM_ROWS; colPos++) {
            for (int rowPos = 0; rowPos < CrafterBlockEntity.GRID_NUM_ROWS; rowPos++) {
                this.drawSlot(ms,
                        CrafterScreenHandler.GRID_START_X - 1 + colPos * Const.SQ,
                        CrafterScreenHandler.GRID_START_Y - 1 + rowPos * Const.SQ);
            }
        }
        progress.draw(ms, menu.tile.getField(CrafterBlockEntity.Fields.TIMER.ordinal()));
    }
}
