package com.lothrazar.cyclic.client;

import com.lothrazar.cyclic.network.CyclicS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CyclicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistry.register();
        CyclicS2C.register();
        ClientRenderer.register();
    }
}
