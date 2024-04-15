package com.lothrazar.cyclic;

import com.lothrazar.cyclic.event.ItemEvents;
import com.lothrazar.cyclic.lookups.CyclicItemLookup;
import com.lothrazar.cyclic.network.CyclicC2S;
import com.lothrazar.cyclic.registry.*;
import net.fabricmc.api.ModInitializer;

import com.lothrazar.cyclic.lookups.CyclicLookup;
import com.lothrazar.cyclic.config.ConfigRegistry;
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