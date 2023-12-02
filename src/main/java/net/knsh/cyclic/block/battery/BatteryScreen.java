package net.knsh.cyclic.block.battery;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.knsh.cyclic.gui.ButtonMachine;
import net.knsh.cyclic.gui.EnergyBar;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.gui.TextureEnum;
import net.knsh.cyclic.library.util.ChatUtil;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.knsh.cyclic.network.packets.PacketTileData;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryScreen extends ScreenBase<BatteryScreenHandler> {
    private ButtonMachine btnToggle;
    private EnergyBar energy;
    private ButtonMachine btnU;
    private ButtonMachine btnD;
    private ButtonMachine btnN;
    private ButtonMachine btnS;
    private ButtonMachine btnE;
    private ButtonMachine btnW;

    public BatteryScreen(BatteryScreenHandler screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.energy = new EnergyBar(this.font, BatteryBlockEntity.MAX);
        energy.guiLeft = leftPos;
        energy.guiTop = topPos;
        int x = leftPos + 132, y = topPos + 8;
        int size = 14;
        btnToggle = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            menu.tile.setFlowing((menu.tile.getFlowing() + 1) % 2);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(BatteryBlockEntity.Fields.FLOWING.ordinal(), menu.tile.getFlowing(), menu.tile.getBlockPos())));
        }));
        x = leftPos + 18;
        y = topPos + 18;
        btnU = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.U.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnU.setTooltip(ChatUtil.lang("gui.cyclic.flowing.up"));
        y = topPos + 60;
        btnD = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.D.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnD.setTooltip(ChatUtil.lang("gui.cyclic.flowing.down"));
        int xCenter = leftPos + 80;
        int yCenter = topPos + 38;
        int space = 18;
        x = xCenter;
        y = yCenter - space;
        btnN = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.N.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnN.setTooltip(ChatUtil.lang("gui.cyclic.flowing.north"));
        x = xCenter;
        y = yCenter + space;
        btnS = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.S.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnS.setTooltip(ChatUtil.lang("gui.cyclic.flowing.south"));
        //now east west
        x = xCenter + space;
        y = yCenter;
        btnE = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.E.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnE.setTooltip(ChatUtil.lang("gui.cyclic.flowing.east"));
        x = xCenter - space;
        y = yCenter;
        btnW = addRenderableWidget(new ButtonMachine(x, y, size, size, "", (p) -> {
            int f = BatteryBlockEntity.Fields.W.ordinal();
            menu.tile.setField(f, menu.tile.getField(f) + 1);
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(f, menu.tile.getField(f), menu.tile.getBlockPos())));
        }));
        btnW.setTooltip(ChatUtil.lang("gui.cyclic.flowing.west"));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, menu.getEnergy());
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        btnToggle.setTooltip(ChatUtil.lang("gui.cyclic.flowing" + menu.tile.getFlowing()));
        btnToggle.setTextureId(menu.tile.getFlowing() == 1 ? TextureEnum.POWER_MOVING : TextureEnum.POWER_STOP);
        btnU.setTextureId(getTextureId(BatteryBlockEntity.Fields.U));
        btnD.setTextureId(getTextureId(BatteryBlockEntity.Fields.D));
        btnN.setTextureId(getTextureId(BatteryBlockEntity.Fields.N));
        btnS.setTextureId(getTextureId(BatteryBlockEntity.Fields.S));
        btnE.setTextureId(getTextureId(BatteryBlockEntity.Fields.E));
        btnW.setTextureId(getTextureId(BatteryBlockEntity.Fields.W));
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
    }

    private TextureEnum getTextureId(Enum<BatteryBlockEntity.Fields> field) {
        return menu.tile.getField(field.ordinal()) == 1 ? TextureEnum.POWER_MOVING : TextureEnum.POWER_STOP;
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        this.drawSlot(ms, 133, 53, CyclicTextures.SLOT_CHARGE);
        energy.draw(ms, menu.getEnergy());
    }
}
