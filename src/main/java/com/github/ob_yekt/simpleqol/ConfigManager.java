package com.github.ob_yekt.simpleqol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import com.github.ob_yekt.simpleqol.mixin.worldgen.BiomeReplacementMixin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("mods")
            .resolve("simpleqol_config.json");

    public static class Config {
        // BIOME REPLACEMENTS
        public Map<String, String> biomeReplacements = new HashMap<>();
        // TIME
        public long dayTicks = 24000;
        public long nightTicks = 12000;
        public long tickCounter = 0;
        // MISC
        public boolean endermanGriefing = false;
        // PHANTOMS
        public int overworldPhantomSpawnWeight = 2; // 0 = disabled (doesn't disable vanilla spawning), 5 = witch weight, 10 = enderman, 100 = zombie
        public int endPhantomSpawnWeight = 1; // Default = 1; 0 disables
        // PHANTOM PACK SIZE
        public int overworldPhantomMinPackSize = 1;
        public int overworldPhantomMaxPackSize = 2;
        public int endPhantomMinPackSize = 1;
        public int endPhantomMaxPackSize = 1;
        // doInsomnia
        public boolean doInsomnia = false; // Gamerule doInsomnia adjusts phantom spawning when the player has not slept
        // TORCHFLOWER
        public int torchflowerBrightness = 14;
        public int pottedTorchflowerBrightness = 14;
        public int torchflowerStage0Brightness = 1;
        public int torchflowerStage1Brightness = 7;
        public int torchflowerStage2Brightness = 14;
        // EYEBLOSSOM
        public int openEyeblossomBrightness = 4;
        public int closedEyeblossomBrightness = 2;
        public int pottedOpenEyeblossomBrightness = 4;
        public int pottedClosedEyeblossomBrightness = 2;
        // PITCHER PLANT
        public int pitcherPlantBrightness = 9;
        public int pitcherCropStage0Brightness = 1;
        public int pitcherCropStage1Brightness = 2;
        public int pitcherCropStage2Brightness = 3;
        public int pitcherCropStage3Brightness = 6;
        public int pitcherCropStage4Brightness = 9;
    }

    public static void populateDefaults() {
        if (config.biomeReplacements.isEmpty()) {
            config.biomeReplacements.put("minecraft:stony_shore", "minecraft:beach");
            config.biomeReplacements.put("minecraft:windswept_gravelly_hills", "minecraft:windswept_hills");
            // Add more defaults as needed
        }
    }

    private static Config config = new Config();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Config loadedConfig = GSON.fromJson(reader, Config.class);
                if (loadedConfig != null) {
                    config = loadedConfig;

                    // Sanitize values
                    if (config.tickCounter < 0) config.tickCounter = 0;
                    if (config.overworldPhantomSpawnWeight < 0) config.overworldPhantomSpawnWeight = 0;
                    if (config.endPhantomSpawnWeight < 0) config.endPhantomSpawnWeight = 0;
                    if (config.overworldPhantomMinPackSize < 1) config.overworldPhantomMinPackSize = 1;
                    if (config.overworldPhantomMaxPackSize < config.overworldPhantomMinPackSize) {
                        config.overworldPhantomMaxPackSize = config.overworldPhantomMinPackSize;
                    }
                    if (config.endPhantomMinPackSize < 1) config.endPhantomMinPackSize = 1;
                    if (config.endPhantomMaxPackSize < config.endPhantomMinPackSize) {
                        config.endPhantomMaxPackSize = config.endPhantomMinPackSize;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Always populate defaults before saving
        populateDefaults();
        save();
    }


    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Accessors

    public static long getDayTicks() {
        return config.dayTicks;
    }

    public static void setDayTicks(long ticks) {
        config.dayTicks = Math.max(1, ticks);
    }

    public static long getNightTicks() {
        return config.nightTicks;
    }

    public static void setNightTicks(long ticks) {
        config.nightTicks = Math.max(1, ticks);
    }

    public static long getTickCounter() {
        return config.tickCounter;
    }

    public static void setTickCounter(long ticks) {
        config.tickCounter = Math.max(0, ticks);
    }

    // BIOMES
    public static Map<String, String> getBiomeReplacements() {
        return config.biomeReplacements;
    }

    // ENDERMAN GRIEF
    public static boolean isEndermanGriefingAllowed() {
        return config.endermanGriefing;
    }

    // PHANTOM CONFIGURATION
    public static int getOverworldPhantomSpawnWeight() {
        return config.overworldPhantomSpawnWeight;
    }

    public static int getEndPhantomSpawnWeight() {
        return config.endPhantomSpawnWeight;
    }

    public static int getOverworldPhantomMinPackSize() {
        return Math.max(1, config.overworldPhantomMinPackSize);
    }

    public static int getOverworldPhantomMaxPackSize() {
        return Math.max(getOverworldPhantomMinPackSize(), config.overworldPhantomMaxPackSize);
    }

    public static int getEndPhantomMinPackSize() {
        return Math.max(1, config.endPhantomMinPackSize);
    }

    public static int getEndPhantomMaxPackSize() {
        return Math.max(getEndPhantomMinPackSize(), config.endPhantomMaxPackSize);
    }

    public static boolean isDoInsomniaEnabled() {
        return config.doInsomnia;
    }

    // TORCHFLOWER
    public static int getTorchflowerBrightness() {
        return config.torchflowerBrightness;
    }

    public static int getTorchflowerStage0Brightness() {
        return config.torchflowerStage0Brightness;
    }

    public static int getTorchflowerStage1Brightness() {
        return config.torchflowerStage1Brightness;
    }

    public static int getTorchflowerStage2Brightness() {
        return config.torchflowerStage2Brightness;
    }

    public static int getPottedTorchflowerBrightness() {
        return config.pottedTorchflowerBrightness;
    }

    //EYEBLOSSOM
    public static int getOpenEyeblossomBrightness() {
        return config.openEyeblossomBrightness;
    }

    public static int getClosedEyeblossomBrightness() {
        return config.closedEyeblossomBrightness;
    }

    public static int getPottedOpenEyeblossomBrightness() {
        return config.pottedOpenEyeblossomBrightness;
    }

    public static int getPottedClosedEyeblossomBrightness() {
        return config.pottedClosedEyeblossomBrightness;
    }

    // PITCHER PLANT
    public static int getPitcherPlantBrightness() {
        return config.pitcherPlantBrightness;
    }

    public static int getPitcherCropStage0Brightness() {
        return config.pitcherCropStage0Brightness;
    }

    public static int getPitcherCropStage1Brightness() {
        return config.pitcherCropStage1Brightness;
    }

    public static int getPitcherCropStage2Brightness() {
        return config.pitcherCropStage2Brightness;
    }

    public static int getPitcherCropStage3Brightness() {
        return config.pitcherCropStage3Brightness;
    }

    public static int getPitcherCropStage4Brightness() {
        return config.pitcherCropStage4Brightness;
    }
}