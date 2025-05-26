package com.github.ob_yekt.simpleqol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("mods")
            .resolve("simpleqol_config.json");

    public static class Config {
        public boolean endermanGriefing = false;
        public long dayTicks = 24000;
        public long nightTicks = 12000;
        public long tickCounter = 0;
    }

    private static Config config = new Config();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Config loadedConfig = GSON.fromJson(reader, Config.class);
                if (loadedConfig != null) {
                    config = loadedConfig;
                    // Ensure tickCounter is non-negative
                    if (config.tickCounter < 0) {
                        config.tickCounter = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Always save after loading to ensure file exists and any corrections are persisted
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

    // Accessors - no automatic saving, caller decides when to save
    public static boolean isEndermanGriefingAllowed() {
        return config.endermanGriefing;
    }

    public static void setEndermanGriefing(boolean value) {
        config.endermanGriefing = value;
    }

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
}