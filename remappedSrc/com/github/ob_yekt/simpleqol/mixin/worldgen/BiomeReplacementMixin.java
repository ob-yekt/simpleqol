package com.github.ob_yekt.simpleqol.mixin.worldgen;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(MultiNoiseBiomeSource.class)
public class BiomeReplacementMixin {

    @Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
    private void replaceBiomes(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        RegistryEntry<Biome> originalBiome = cir.getReturnValue();

        if (originalBiome != null && originalBiome.getKey().isPresent()) {
            Identifier originalId = originalBiome.getKey().get().getValue();
            String originalIdStr = originalId.toString();

            Map<String, String> replacements = ConfigManager.getBiomeReplacements();
            if (replacements.containsKey(originalIdStr)) {
                String replacementIdStr = replacements.get(originalIdStr);
                Identifier replacementId = Identifier.tryParse(replacementIdStr);
                if (replacementId != null) {
                    MultiNoiseBiomeSource source = (MultiNoiseBiomeSource) (Object) this;
                    source.getBiomes().stream()
                            .filter(entry -> entry.getKey().isPresent() &&
                                    entry.getKey().get().getValue().equals(replacementId))
                            .findFirst()
                            .ifPresent(cir::setReturnValue);
                }
            }
        }
    }
}
