package net.knsh.cyclic;

import net.fabricmc.api.ModInitializer;

import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.config.ConfigRegistry;
import net.knsh.cyclic.event.ItemEvents;
import net.knsh.cyclic.network.CyclicC2S;
import net.knsh.cyclic.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cyclic implements ModInitializer {
	public static final String MOD_ID = "cyclic";
    public static final Logger LOGGER = LoggerFactory.getLogger("cyclic");

	@Override
	public void onInitialize() {
		ConfigRegistry cfg = new ConfigRegistry();
		cfg.setupMain();
		cfg.setupClient();

		CyclicBlocks.register();
		CyclicBlockEntities.register();
		CyclicEntities.register();
		CyclicRecipeTypes.register();
		CyclicEnchants.register();
		CyclicFluids.register();
		CyclicItems.register();
		CyclicSounds.register();
		CyclicScreens.register();
		CyclicTabGroups.register();
		CyclicC2S.register();

		ItemEvents.register();

		CyclicItemLookup.init();
		CyclicLookup.init();
	}
}