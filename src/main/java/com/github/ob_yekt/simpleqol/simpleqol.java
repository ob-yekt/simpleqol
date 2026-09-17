package com.github.ob_yekt.simpleqol;

import com.github.ob_yekt.simpleqol.elytra.ElytraFlightHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRules;
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
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            TimeController.init(server);

            // Sync spawn_phantoms gamerule with config
            server.getGameRules().set(
                    GameRules.SPAWN_PHANTOMS,
                    ConfigManager.isDoInsomniaEnabled(),
                    server
            );
        });

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

                if (item != Items.AIR && chance > 0) {
                    // Composting is a data component since 26.3; it can only reference a
                    // registered context_int_provider, so snap to the nearest vanilla chance tier.
                    Compostable compostable = new Compostable(nearestCompostTier(chance));
                    DefaultItemComponentEvents.MODIFY.register(context ->
                            context.modify(item, builder -> builder.set(DataComponents.COMPOSTABLE, compostable)));
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

    private static ResourceKey<ContextIntProvider> nearestCompostTier(float chance) {
        if (chance < 0.4f) return ContextIntProviders.COMPOSTABLE_LOW;          // 0.30
        if (chance < 0.575f) return ContextIntProviders.COMPOSTABLE_LOW_MEDIUM; // 0.50
        if (chance < 0.75f) return ContextIntProviders.COMPOSTABLE_MEDIUM;      // 0.65
        if (chance < 0.925f) return ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH; // 0.85
        return ContextIntProviders.COMPOSTABLE_ALWAYS_ADD_ONE;                  // 1.00
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}