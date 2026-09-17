package com.github.ob_yekt.simpleqol.mixin.worldgen;

import com.github.ob_yekt.simpleqol.ConfigManager;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(MultiNoiseBiomeSource.class)
public class BiomeReplacementMixin {

    // Since 26.3 every BiomeResolver (plain and per-chunk) funnels into the
    // Climate.TargetPoint overload, so hooking it covers all biome lookups.
    @Inject(
            method = "getNoiseBiome(Lnet/minecraft/world/level/biome/Climate$TargetPoint;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void replaceBiomes(Climate.TargetPoint target, CallbackInfoReturnable<Holder<Biome>> cir) {
        Holder<Biome> originalBiome = cir.getReturnValue();
        if (originalBiome == null) {
            return;
        }

        Optional<ResourceKey<Biome>> originalKey = originalBiome.unwrapKey();
        if (originalKey.isEmpty()) {
            return;
        }

        String originalIdStr = originalKey.get().identifier().toString();

        Map<String, String> replacements = ConfigManager.getBiomeReplacements();
        String replacementIdStr = replacements.get(originalIdStr);
        if (replacementIdStr == null) {
            return;
        }

        Identifier replacementId = Identifier.tryParse(replacementIdStr);
        if (replacementId == null) {
            return;
        }

        MultiNoiseBiomeSource source = (MultiNoiseBiomeSource) (Object) this;
        source.possibleBiomes().stream()
                .filter(holder -> holder.unwrapKey()
                        .map(key -> key.identifier().equals(replacementId))
                        .orElse(false))
                .findFirst()
                .ifPresent(cir::setReturnValue);
    }
}