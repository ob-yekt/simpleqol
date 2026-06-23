package com.github.ob_yekt.simpleqol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simpleqol implements ModInitializer {
	public static final String MOD_ID = "simpleqol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing simpleqol Mod");

		// Initialize config
		ConfigManager.load();

		// Register server start callback to initialize TimeController with server instance
        // Custom day and night
        ServerLifecycleEvents.SERVER_STARTED.register(TimeController::init);

		// Commands
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        simpleqolCommand.register(dispatcher)
        );

        // --- PHANTOM CONFIG INTEGRATION ---
        int overworldWeight = ConfigManager.getOverworldPhantomSpawnWeight();
        int endWeight = ConfigManager.getEndPhantomSpawnWeight();

        // 1. Inject into Overworld if weight is greater than 0
        if (overworldWeight > 0) {
            BiomeModifications.addSpawn(
                    BiomeSelectors.foundInOverworld(),
                    MobCategory.MONSTER,
                    EntityTypes.PHANTOM,
                    overworldWeight,
                    ConfigManager.getOverworldPhantomMinPackSize(),
                    ConfigManager.getOverworldPhantomMaxPackSize()
            );
        }

        // 2. Inject into The End if weight is greater than 0
        if (endWeight > 0) {
            BiomeModifications.addSpawn(
                    BiomeSelectors.foundInTheEnd(),
                    MobCategory.MONSTER,
                    EntityTypes.PHANTOM,
                    endWeight,
                    ConfigManager.getEndPhantomMinPackSize(),
                    ConfigManager.getEndPhantomMaxPackSize()
            );
        }

		// Custom compost foods
        ConfigManager.getCompostableItems().forEach((id, chance) -> {
            Identifier identifier = Identifier.tryParse(id);

            if (identifier != null) {
                Item item = BuiltInRegistries.ITEM.getValue(identifier);

                if (item != Items.AIR) {
                    CompostableRegistry.INSTANCE.add(item, chance);
                }
            }
        });

        /// TICK EVENTS FOR ELYTRA
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ElytraFlightHandler.tick(player);
            }
        });
	}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}