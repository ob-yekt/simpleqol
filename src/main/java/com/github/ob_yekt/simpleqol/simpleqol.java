package com.github.ob_yekt.simpleqol;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simpleqol implements ModInitializer {
	public static final String MOD_ID = "simpleqol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing simpleqol Mod");
		// Recipe registration is handled via data generation (see simpleqolRecipeGenerator)
		// Add other QOL feature initializations here
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}