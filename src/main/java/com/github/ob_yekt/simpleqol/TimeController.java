package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.world.rule.GameRules.ADVANCE_TIME;

public class TimeController implements ServerTickEvents.StartTick {
    private static final Logger LOGGER = LoggerFactory.getLogger("simpleqol");
    private static boolean initialized = false;
    private static int stabilizationTicks = 20; // Wait 1 second (20 ticks) before applying custom time

    public static void init(MinecraftServer server) {
        ConfigManager.load(); // Load config (also saves to ensure file exists)
        dayTicks = ConfigManager.getDayTicks();
        nightTicks = ConfigManager.getNightTicks();
        tickCounter = ConfigManager.getTickCounter();
        ServerTickEvents.START_SERVER_TICK.register(new TimeController());
        LOGGER.info("TimeController initialized with dayTicks: {}, nightTicks: {}, tickCounter: {}", dayTicks, nightTicks, tickCounter);
    }

    private static long tickCounter;
    private static long visualTime;
    private static long dayTicks;
    private static long nightTicks;

    private static void updateVisualTime(MinecraftServer server) {
        long totalCycle = dayTicks + nightTicks;
        long tickOfCycle = tickCounter % totalCycle;

        if (tickOfCycle < dayTicks) {
            // Day phase: map 0–dayTicks to 0–12542 (when sleep becomes possible)
            visualTime = (tickOfCycle * 12542L) / dayTicks;
        } else {
            // Night phase: map dayTicks–(dayTicks+nightTicks) to 12542–23999 (sleep allowed period)
            long nightElapsed = tickOfCycle - dayTicks;
            visualTime = 12542L + (nightElapsed * (23999L - 12542L)) / nightTicks;
        }

        for (ServerWorld world : server.getWorlds()) {
            world.setTimeOfDay(visualTime);
            for (ServerPlayerEntity player : world.getPlayers()) {
                player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(
                        world.getTime(), visualTime, false
                ));
            }
        }
        LOGGER.debug("Updated visualTime: {}, tickCounter: {}", visualTime, tickCounter);
    }

    @Override
    public void onStartTick(MinecraftServer server) {
        if (!initialized && server.getWorlds().iterator().hasNext()) {
            // Initialize tickCounter to match worldTime directly
            ServerWorld world = server.getWorlds().iterator().next();
            long worldTime = world.getTimeOfDay();
            tickCounter = Math.max(0, worldTime);
            LOGGER.info("First tick sync: tickCounter: {} for worldTime: {}", tickCounter, worldTime);
            initialized = true;
        }

        if (stabilizationTicks > 0) {
            // Skip time updates for first 20 ticks to stabilize world time
            stabilizationTicks--;
            LOGGER.debug("Stabilization tick: {}, worldTime: {}", stabilizationTicks, server.getWorlds().iterator().next().getTimeOfDay());
            return;
        }

        // Check if world time was changed (e.g., by /time set command)
        for (ServerWorld world : server.getWorlds()) {
            long worldTime = world.getTimeOfDay();
            if (Math.abs(worldTime - visualTime) > 5) { // Allow small discrepancies
                // Sync tickCounter to match the new world time
                syncTickCounterToWorldTime(worldTime);
                LOGGER.info("Detected time change - synced tickCounter: {} for worldTime: {}", tickCounter, worldTime);
                // Save the new state immediately since this is an important change
                ConfigManager.setTickCounter(tickCounter);
                ConfigManager.save();
            }
            break; // Check only one world
        }

        tickCounter++;

        // Disable vanilla daylight cycle once
        if (tickCounter == 1) {
            for (ServerWorld world : server.getWorlds()) {
                world.getGameRules().setValue(ADVANCE_TIME, false, world.getServer());
            }
        }

        // Update time
        updateVisualTime(server);
    }

    private static void syncTickCounterToWorldTime(long worldTime) {
        if (worldTime < 12542L) {
            // Day phase: map 0–12542 to 0–dayTicks
            tickCounter = (worldTime * dayTicks) / 12542L;
        } else {
            // Night phase: map 12542–23999 to dayTicks–(dayTicks+nightTicks)
            long nightTime = worldTime - 12542L;
            tickCounter = dayTicks + (nightTime * nightTicks) / (23999L - 12542L);
        }
        tickCounter = Math.max(0, tickCounter);
    }

    public static void resetTime() {
        tickCounter = 0;
        ConfigManager.setTickCounter(0);
        ConfigManager.save();
        LOGGER.info("Reset tickCounter to 0");
    }

    public static long getDayTicks() {
        return dayTicks;
    }

    public static long getNightTicks() {
        return nightTicks;
    }

    public static void setDayTicks(long newDayTicks, MinecraftServer server) {
        dayTicks = Math.max(1, newDayTicks);

        // Recalculate tickCounter to maintain current world time
        if (server.getWorlds().iterator().hasNext()) {
            ServerWorld world = server.getWorlds().iterator().next();
            long worldTime = world.getTimeOfDay();
            syncTickCounterToWorldTime(worldTime);
            LOGGER.info("Set dayTicks: {}, recalculated tickCounter: {} for worldTime: {}", dayTicks, tickCounter, worldTime);
        }

        // Save both settings
        ConfigManager.setDayTicks(dayTicks);
        ConfigManager.setTickCounter(tickCounter);
        ConfigManager.save();
        updateVisualTime(server);
    }

    public static void setNightTicks(long newNightTicks, MinecraftServer server) {
        nightTicks = Math.max(1, newNightTicks);

        // Recalculate tickCounter to maintain current world time
        if (server.getWorlds().iterator().hasNext()) {
            ServerWorld world = server.getWorlds().iterator().next();
            long worldTime = world.getTimeOfDay();
            syncTickCounterToWorldTime(worldTime);
            LOGGER.info("Set nightTicks: {}, recalculated tickCounter: {} for worldTime: {}", nightTicks, tickCounter, worldTime);
        }

        // Save both settings
        ConfigManager.setNightTicks(nightTicks);
        ConfigManager.setTickCounter(tickCounter);
        ConfigManager.save();
        updateVisualTime(server);
    }

    // Called when server is shutting down to save final state
    public static void onServerStop() {
        ConfigManager.setTickCounter(tickCounter);
        ConfigManager.save();
        LOGGER.info("Saved final tickCounter state: {}", tickCounter);
    }
}