package com.lothrazar.cyclic.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.lothrazar.cyclic.block.BlockEntityCyclic;
import com.lothrazar.cyclic.network.PacketIdentifiers;
import com.lothrazar.cyclic.network.packets.PacketTileData;
import net.minecraft.core.BlockPos;

public class ButtonMachineField extends ButtonMachine {
    BlockPos tilePos;
    private final TextureEnum textureZero;
    private final TextureEnum textureOne;
    private final TextureEnum textureTwo = TextureEnum.RENDER_OUTLINE;
    private final String tooltipPrefix;

    public ButtonMachineField(int xPos, int yPos, int field, BlockPos pos) {
        this(xPos, yPos, field, pos, TextureEnum.REDSTONE_ON, TextureEnum.REDSTONE_NEEDED, "gui.cyclic.redstone");
    }

    public ButtonMachineField(int xPos, int yPos, int field, BlockPos pos, TextureEnum toff, TextureEnum tonn, String tooltipPrefix) {
        super(xPos, yPos, 20, 20, "", (p) -> {
            //save included
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, PacketTileData.encode(new PacketTileData(field, pos)));
        });
        this.tilePos = pos;
        this.setTileField(field);
        this.textureZero = toff;
        this.textureOne = tonn;
        this.tooltipPrefix = tooltipPrefix;
    }

    public ButtonMachineField setSize(int size) {
        this.height = size;
        this.width = size;
        return this;
    }

    public void onValueUpdate(BlockEntityCyclic tile) {
        int val = tile.getField(this.getTileField());
        this.onValueUpdate(val);
    }

    private void onValueUpdate(int val) {
        setTooltip(this.tooltipPrefix + val);
        switch (val) {
            case 0:
                setTextureId(textureZero);
                break;
            case 1:
                setTextureId(textureOne);
                break;
            case 2:
                setTextureId(textureTwo);
                break;
        }
    }
}
