package com.github.ob_yekt.simpleqol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simpleqol implements ModInitializer {
	public static final String MOD_ID = "simpleqol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing simpleqol Mod");

		// Register server start callback to initialize TimeController with server instance
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			TimeController.init(server); // Custom day and night
		});

		// Commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			simpleqolCommand.register(dispatcher);
		});

		// Poisonous potato can now be composted
		CompostingChanceRegistry.INSTANCE.add(Items.POISONOUS_POTATO, 0.65f);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}