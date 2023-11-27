package net.knsh.cyclic.block.antipotion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.knsh.cyclic.block.beaconpotion.RenderBeaconPotion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderAntiBeacon implements BlockEntityRenderer<AntiBeaconBlockEntity> {

    public RenderAntiBeacon(BlockEntityRendererProvider.Context d) {}

    @Override
    public void render(AntiBeaconBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        long i = blockEntity.getLevel().getGameTime();
        if (blockEntity.isPowered()) { // if im NOT powered, im running
            return; // do not render if redstone offed
        }
        List<BeaconBlockEntity.BeaconBeamSection> list = blockEntity.getBeamSections();
        int j = 0;
        for (int k = 0; k < list.size(); ++k) {
            BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = list.get(k);
            RenderBeaconPotion.renderBeaconBeam(poseStack, buffer, partialTick, i, j, k == list.size() - 1 ? 1024 : beaconblockentity$beaconbeamsection.getHeight(), beaconblockentity$beaconbeamsection.getColor());
            j += beaconblockentity$beaconbeamsection.getHeight();
        }
    }
}
