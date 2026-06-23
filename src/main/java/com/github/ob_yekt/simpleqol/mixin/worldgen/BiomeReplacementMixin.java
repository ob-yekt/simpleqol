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

    // getNoiseBiome is overloaded (there's also a Climate.TargetPoint variant), so we
    // need the full descriptor to target the (int, int, int, Climate.Sampler) overload.
    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void replaceBiomes(int quartX, int quartY, int quartZ, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
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