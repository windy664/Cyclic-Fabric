package net.knsh.cyclic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.knsh.cyclic.network.CyclicS2C;

@Environment(EnvType.CLIENT)
public class CyclicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistry.register();
        CyclicS2C.register();
    }
}
