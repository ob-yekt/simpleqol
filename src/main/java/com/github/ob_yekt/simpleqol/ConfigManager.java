package com.github.ob_yekt.simpleqol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("config")
            .resolve("simpleqol_config.json");

    public static class Config {
        // BIOME REPLACEMENTS
        public Map<String, String> biomeReplacements = new HashMap<>();

        // TIME
        public long dayTicks = 24000;
        public long nightTicks = 12000;
        public long tickCounter = 0;

        // MISC
        public float chargedCreeperChance = 0.02f;
        public boolean endermanGriefing = false;
        public boolean sweetberrybushDamage = false;
        public boolean librarianRebalance = true;

        // ENCHANTING TABLE
        public int maxEnchantingBookshelves = 12;

        // LEAF LITTER
        public float leafLitterMultiplier = 0.25f;

        // LEAF DECAY
        public boolean instantLeafDecay = true;

        // doInsomnia
        public boolean doInsomnia = false;

        // ELYTRA
        public boolean doVanillaElytra = false;
        public float customElytraFlyingSpeed = 0.012f;

        // PHANTOM SPAWN WEIGHTS
        public int overworldPhantomSpawnWeight = 8;
        public int endPhantomSpawnWeight = 1;

        // PHANTOM PACK SIZE
        public int overworldPhantomMinPackSize = 1;
        public int overworldPhantomMaxPackSize = 4;
        public int endPhantomMinPackSize = 1;
        public int endPhantomMaxPackSize = 1;

        // CUSTOM COMPOSTABLES (item id -> composting chance, 0.0-1.0)
        public Map<String, Float> compostableItems = new HashMap<>();
    }

    private static Config config = new Config();

    public static void populateDefaults() {
        if (config.compostableItems == null) {
            config.compostableItems = new HashMap<>();
        }

        // Rotten Flesh & Poisonous Potato
        config.compostableItems.put("minecraft:rotten_flesh", 0.5f);
        config.compostableItems.put("minecraft:poisonous_potato", 0.65f);

        // Fish
        config.compostableItems.put("minecraft:cod", 0.5f);
        config.compostableItems.put("minecraft:salmon", 0.5f);
        config.compostableItems.put("minecraft:tropical_fish", 0.5f);
        config.compostableItems.put("minecraft:pufferfish", 0.5f);
        config.compostableItems.put("minecraft:cooked_cod", 0.65f);
        config.compostableItems.put("minecraft:cooked_salmon", 0.65f);

        // Meats
        config.compostableItems.put("minecraft:beef", 0.5f);
        config.compostableItems.put("minecraft:porkchop", 0.5f);
        config.compostableItems.put("minecraft:chicken", 0.5f);
        config.compostableItems.put("minecraft:mutton", 0.5f);
        config.compostableItems.put("minecraft:rabbit", 0.5f);
        config.compostableItems.put("minecraft:cooked_beef", 0.65f);
        config.compostableItems.put("minecraft:cooked_porkchop", 0.65f);
        config.compostableItems.put("minecraft:cooked_chicken", 0.65f);
        config.compostableItems.put("minecraft:cooked_mutton", 0.65f);
        config.compostableItems.put("minecraft:cooked_rabbit", 0.65f);

        // Eggs
        config.compostableItems.put("minecraft:egg", 0.65f);
    }

    public static void load() {
        boolean isFirstGeneration = !Files.exists(CONFIG_PATH);

        if (!isFirstGeneration) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Config loadedConfig = GSON.fromJson(reader, Config.class);
                if (loadedConfig != null) {
                    config = loadedConfig;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            populateDefaults();
        }

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

    public static Map<String, String> getBiomeReplacements() {
        return config.biomeReplacements;
    }

    public static float getChargedCreeperChance() {
        return config.chargedCreeperChance;
    }

    public static boolean isEndermanGriefingAllowed() {
        return config.endermanGriefing;
    }

    public static boolean isSweetBerryBushDamageAllowed() {
        return config.sweetberrybushDamage;
    }

    public static boolean isLibrarianRebalanceEnabled() {
        return config.librarianRebalance;
    }

    public static int getMaxEnchantingBookshelves() {
        return config.maxEnchantingBookshelves;
    }

    public static boolean isInstantLeafDecayEnabled() {
        return config.instantLeafDecay;
    }

    public static int getOverworldPhantomSpawnWeight() {
        return config.overworldPhantomSpawnWeight;
    }

    public static int getEndPhantomSpawnWeight() {
        return config.endPhantomSpawnWeight;
    }

    public static int getOverworldPhantomMinPackSize() {
        return config.overworldPhantomMinPackSize;
    }

    public static int getOverworldPhantomMaxPackSize() {
        return config.overworldPhantomMaxPackSize;
    }

    public static int getEndPhantomMinPackSize() {
        return config.endPhantomMinPackSize;
    }

    public static int getEndPhantomMaxPackSize() {
        return config.endPhantomMaxPackSize;
    }

    public static boolean isDoInsomniaEnabled() {
        return config.doInsomnia;
    }

    public static boolean isVanillaElytraEnabled() {
        return config.doVanillaElytra;
    }

    public static float getCustomElytraFlyingSpeed() {
        return config.customElytraFlyingSpeed;
    }

    public static Map<String, Float> getCompostableItems() {
        return config.compostableItems;
    }

    public static float getLeafLitterMultiplier() {
        return config.leafLitterMultiplier;
    }
}