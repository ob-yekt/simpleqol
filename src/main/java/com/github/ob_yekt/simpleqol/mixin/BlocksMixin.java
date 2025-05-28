package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchflowerBlock;
import net.minecraft.block.PitcherCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Unique
    private static boolean configLoaded = false;

    @Unique
    private static void ensureConfigLoaded() {
        if (!configLoaded) {
            ConfigManager.load();
            configLoaded = true;
        }
    }
    // Torchflower - Uses config value
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=torchflower")))
    private static AbstractBlock.Settings modifyTorchflower(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getTorchflowerBrightness());
    }

    // Torchflower Crop - Uses config values based on growth stage
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=torchflower_crop")))
    private static AbstractBlock.Settings modifyTorchflowerCrop(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> {
            int age = blockState.get(TorchflowerBlock.AGE);
            return switch (age) {
                case 0 -> ConfigManager.getTorchflowerStage0Brightness();
                case 1 -> ConfigManager.getTorchflowerStage1Brightness();
                case 2 -> ConfigManager.getTorchflowerStage2Brightness();
                default -> ConfigManager.getTorchflowerBrightness();
            };
        });
    }

    // Potted Torchflower - Uses config value (ordinal 1 based on example)
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;createFlowerPotSettings()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 1))
    private static AbstractBlock.Settings modifyPottedTorchflower(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getPottedTorchflowerBrightness());
    }

    // Open Eyeblossom - Uses config value
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=open_eyeblossom")))
    private static AbstractBlock.Settings modifyOpenEyeblossom(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getOpenEyeblossomBrightness());
    }

    // Closed Eyeblossom - Uses config value
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=closed_eyeblossom")))
    private static AbstractBlock.Settings modifyClosedEyeblossom(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getClosedEyeblossomBrightness());
    }

    // Potted Open Eyeblossom - Uses config value (ordinal 36 based on example)
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;createFlowerPotSettings()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 36))
    private static AbstractBlock.Settings modifyPottedOpenEyeblossom(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getPottedOpenEyeblossomBrightness());
    }

    // Potted Closed Eyeblossom - Uses config value (ordinal 37 based on example)
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;createFlowerPotSettings()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 37))
    private static AbstractBlock.Settings modifyPottedClosedEyeblossom(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getPottedClosedEyeblossomBrightness());
    }

    // Pitcher Plant - Uses config value
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=pitcher_plant")))
    private static AbstractBlock.Settings modifyPitcherPlant(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> ConfigManager.getPitcherPlantBrightness());
    }

    // Pitcher Crop - Uses config values based on growth stage
    @ModifyExpressionValue(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractBlock$Settings;create()Lnet/minecraft/block/AbstractBlock$Settings;",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=pitcher_crop")))
    private static AbstractBlock.Settings modifyPitcherCrop(AbstractBlock.Settings properties) {
        ensureConfigLoaded();
        return properties.luminance(blockState -> {
            int age = blockState.get(PitcherCropBlock.AGE);
            return switch (age) {
                case 0 -> ConfigManager.getPitcherCropStage0Brightness();
                case 1 -> ConfigManager.getPitcherCropStage1Brightness();
                case 2 -> ConfigManager.getPitcherCropStage2Brightness();
                case 3 -> ConfigManager.getPitcherCropStage3Brightness();
                case 4 -> ConfigManager.getPitcherCropStage4Brightness();
                default -> ConfigManager.getPitcherPlantBrightness();
            };
        });
    }
}