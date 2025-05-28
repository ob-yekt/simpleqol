package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.world.biome.BiomeKeys.END_HIGHLANDS;
import static net.minecraft.world.biome.BiomeKeys.END_MIDLANDS;

public class PhantomManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("simpleqol-phantoms");

    public static void initialize() {
        LOGGER.info("Initializing phantom spawn modifications...");

        int overworldWeight = ConfigManager.getOverworldPhantomSpawnWeight();
        int endWeight = ConfigManager.getEndPhantomSpawnWeight();

        if (overworldWeight > 0) {
            addOverworldPhantomSpawns(overworldWeight);
            LOGGER.info("Overworld phantom spawning: ENABLED (weight: {})", overworldWeight);
        } else {
            LOGGER.info("Overworld phantom spawning: DISABLED");
        }

        if (endWeight > 0) {
            addEndPhantomSpawns(endWeight);
            LOGGER.info("End phantom spawning: ENABLED (weight: {})", endWeight);
        } else {
            LOGGER.info("End phantom spawning: DISABLED");
        }

        // Set doInsomnia gamerule based on config
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            boolean doInsomnia = ConfigManager.isDoInsomniaEnabled();
            for (ServerWorld world : server.getWorlds()) {
                world.getGameRules().get(GameRules.DO_INSOMNIA).set(doInsomnia, server);
            }
            LOGGER.info("Gamerule doInsomnia set to {}", doInsomnia);
        });

        LOGGER.info("Phantom spawn modifications applied successfully");
    }

    private static void addOverworldPhantomSpawns(int weight) {
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER,
                EntityType.PHANTOM,
                weight,
                ConfigManager.getOverworldPhantomMinPackSize(),
                ConfigManager.getOverworldPhantomMaxPackSize()
        );
    }

    private static void addEndPhantomSpawns(int weight) {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(END_HIGHLANDS, END_MIDLANDS),
                SpawnGroup.MONSTER,
                EntityType.PHANTOM,
                weight,
                ConfigManager.getEndPhantomMinPackSize(),
                ConfigManager.getEndPhantomMaxPackSize()
        );
    }
}