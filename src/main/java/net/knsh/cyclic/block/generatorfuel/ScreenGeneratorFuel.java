package net.knsh.cyclic.block.generatorfuel;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.knsh.cyclic.gui.*;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.knsh.cyclic.network.packets.PacketTileData;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenGeneratorFuel extends ScreenBase<ContainerGeneratorFuel> {
    private ButtonMachineField btnRedstone;
    private ButtonMachine btnToggle;
    private EnergyBar energy;
    private TexturedProgress progress;

    public ScreenGeneratorFuel(ContainerGeneratorFuel handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyBar(this.font, TileGeneratorFuel.MAX);
        this.progress = new TexturedProgress(this.font, 76, 60, CyclicTextures.FUEL_PROG);
        energy.visible = true;
        progress.guiLeft = energy.guiLeft = leftPos;
        progress.guiTop = energy.guiTop = topPos;
        int x, y;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, TileGeneratorFuel.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
        x = leftPos + 132;
        y = topPos + 36;
        btnToggle = addRenderableWidget(new ButtonMachine(x, y, 14, 14, "", (p) -> {
            int f = TileGeneratorFuel.Fields.FLOWING.ordinal();
            int tog = (menu.tile.getField(f) + 1) % 2;
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, tog, menu.tile.getBlockPos())));
        }));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, (int) menu.tile.energy.amount);
        progress.renderHoveredToolTip(ms, mouseX, mouseY, menu.tile.getField(TileGeneratorFuel.Fields.TIMER.ordinal()));
        btnRedstone.onValueUpdate(menu.tile);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
        int fld = TileGeneratorFuel.Fields.FLOWING.ordinal();
        btnToggle.setTooltip(Component.translatable("gui.cyclic.flowing" + menu.tile.getField(fld)).getString());
        btnToggle.setTextureId(menu.tile.getField(fld) == 1 ? TextureEnum.POWER_MOVING : TextureEnum.POWER_STOP);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTick, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        this.drawSlotLarge(ms, 70, 30);
        progress.max = menu.tile.getField(TileGeneratorFuel.Fields.BURNMAX.ordinal());
        progress.draw(ms, menu.tile.getField(TileGeneratorFuel.Fields.TIMER.ordinal()));
        energy.draw(ms, menu.tile.energy.amount);
    }
}
