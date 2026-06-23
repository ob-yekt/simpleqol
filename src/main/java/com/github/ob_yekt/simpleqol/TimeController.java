package com.github.ob_yekt.simpleqol;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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

    // The clock we use as the source of truth for sync-detection / initial read.
    // Normally resolves to the overworld's default clock (minecraft:overworld).
    private static Holder<WorldClock> referenceClock;

    /**
     * Finds (and caches) a clock to use for reading back the "current" time, e.g. to detect
     * an external /time set or to seed tickCounter on first load. Picks the first level's
     * default clock it finds.
     */
    private static Optional<Holder<WorldClock>> resolveReferenceClock(MinecraftServer server) {
        if (referenceClock != null) {
            return Optional.of(referenceClock);
        }
        for (ServerLevel level : server.getAllLevels()) {
            Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
            if (clock.isPresent()) {
                referenceClock = clock.get();
                return clock;
            }
        }
        return Optional.empty();
    }

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

        // setTotalTicks() on ServerClockManager broadcasts ClientboundSetTimePacket to all
        // players internally, so there's no manual packet-sending needed anymore.
        for (ServerLevel level : server.getAllLevels()) {
            level.dimensionType().defaultClock().ifPresent(clock -> server.clockManager().setTotalTicks(clock, visualTime));
        }

        LOGGER.debug("Updated visualTime: {}, tickCounter: {}", visualTime, tickCounter);
    }

    @Override
    public void onStartTick(MinecraftServer server) {
        if (!initialized && server.getAllLevels().iterator().hasNext()) {
            // Initialize tickCounter to match the reference clock's current time directly
            resolveReferenceClock(server).ifPresent(clock -> {
                long worldTime = server.clockManager().getTotalTicks(clock);
                tickCounter = Math.max(0, worldTime);
                LOGGER.info("First tick sync: tickCounter: {} for worldTime: {}", tickCounter, worldTime);
            });
            initialized = true;
        }

        if (stabilizationTicks > 0) {
            // Skip time updates for first 20 ticks to stabilize world time
            stabilizationTicks--;
            LOGGER.debug("Stabilization tick: {}", stabilizationTicks);
            return;
        }

        // Check if world time was changed (e.g., by /time set command)
        Optional<Holder<WorldClock>> reference = resolveReferenceClock(server);
        if (reference.isPresent()) {
            long worldTime = server.clockManager().getTotalTicks(reference.get());
            if (Math.abs(worldTime - visualTime) > 5) { // Allow small discrepancies
                // Sync tickCounter to match the new world time
                syncTickCounterToWorldTime(worldTime);
                LOGGER.info("Detected time change - synced tickCounter: {} for worldTime: {}", tickCounter, worldTime);
                // Save the new state immediately since this is an important change
                ConfigManager.setTickCounter(tickCounter);
                ConfigManager.save();
            }
        }

        tickCounter++;

        // Pause vanilla's automatic clock advancement once, per-clock, so our own
        // setTotalTicks() calls are the sole driver of time. This is more surgical than the
        // old "disable doDaylightCycle" trick, since advance_time is now a server-global
        // gamerule that would freeze every clock (including the End's), not just this one.
        if (tickCounter == 1) {
            for (ServerLevel level : server.getAllLevels()) {
                level.dimensionType().defaultClock().ifPresent(clock -> server.clockManager().setPaused(clock, true));
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
        resolveReferenceClock(server).ifPresent(clock -> {
            long worldTime = server.clockManager().getTotalTicks(clock);
            syncTickCounterToWorldTime(worldTime);
            LOGGER.info("Set dayTicks: {}, recalculated tickCounter: {} for worldTime: {}", dayTicks, tickCounter, worldTime);
        });

        // Save both settings
        ConfigManager.setDayTicks(dayTicks);
        ConfigManager.setTickCounter(tickCounter);
        ConfigManager.save();
        updateVisualTime(server);
    }

    public static void setNightTicks(long newNightTicks, MinecraftServer server) {
        nightTicks = Math.max(1, newNightTicks);

        // Recalculate tickCounter to maintain current world time
        resolveReferenceClock(server).ifPresent(clock -> {
            long worldTime = server.clockManager().getTotalTicks(clock);
            syncTickCounterToWorldTime(worldTime);
            LOGGER.info("Set nightTicks: {}, recalculated tickCounter: {} for worldTime: {}", nightTicks, tickCounter, worldTime);
        });

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