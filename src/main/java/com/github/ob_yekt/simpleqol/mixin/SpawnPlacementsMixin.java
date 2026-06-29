package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SpawnPlacements.class)
public class SpawnPlacementsMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void simpleqol$overridePhantomPlacement(CallbackInfo ci) {
        int overworldWeight = ConfigManager.getOverworldPhantomSpawnWeight();
        int endWeight = ConfigManager.getEndPhantomSpawnWeight();

        if (overworldWeight > 0 || endWeight > 0) {
            // Use standard raw types to insert into the private map
            @SuppressWarnings("unchecked")
            Map<EntityType<?>, Object> dataMap = (Map<EntityType<?>, Object>) SpawnPlacementsAccessor.getDataByType();

            // Create the new placement data handle manually via the internal private constructor invocation trick,
            // or we can invoke the private register method directly by removing it from the map first!

            // Shortcut: Remove the vanilla registration, allowing us to use vanilla's register method again!
            dataMap.remove(EntityTypes.PHANTOM);

            SpawnPlacements.register(
                    EntityTypes.PHANTOM,
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING,
                    (entityType, world, spawnReason, pos, random) -> {
                        if (world.getDifficulty() == Difficulty.PEACEFUL) return false;

                        // --- Biome Guardrails ---
                        Holder<@NotNull Biome> biomeHolder = world.getBiome(pos);
                        if (biomeHolder.is(Biomes.MUSHROOM_FIELDS) || biomeHolder.is(Biomes.DEEP_DARK)) {
                            return false;
                        }

                        // If we are in the End, skip the light level checks entirely
                        if (world.getLevel().dimension() == net.minecraft.world.level.Level.END) {
                            return true;
                        }

                        // Mimic standard monster light checks (accounts for time of day + block light)
                        // It checks if the total current brightness is <= 7 (or 0 depending on the MC version)
                        // Note: Modern MC versions require block light to be 0 for monsters.
                        if (world.getBrightness(LightLayer.BLOCK, pos) > 0) {
                            return false;
                        }

                        // This properly checks the current time-adjusted sky light
                        return world.getMaxLocalRawBrightness(pos) <= 7;
                    }
            );
        }
    }
}