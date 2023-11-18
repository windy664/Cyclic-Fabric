package net.knsh.cyclic;

import net.fabricmc.api.ModInitializer;

import net.knsh.cyclic.network.CyclicC2S;
import net.knsh.cyclic.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cyclic implements ModInitializer {
	public static final String MOD_ID = "cyclic";
    public static final Logger LOGGER = LoggerFactory.getLogger("cyclic");

	@Override
	public void onInitialize() {
		CyclicBlocks.register();
		CyclicBlockEntities.register();
		CyclicEntities.register();
		CyclicFluids.register();
		CyclicItems.register();
		CyclicScreens.register();
		CyclicTabGroups.register();
		CyclicC2S.register();
	}
}