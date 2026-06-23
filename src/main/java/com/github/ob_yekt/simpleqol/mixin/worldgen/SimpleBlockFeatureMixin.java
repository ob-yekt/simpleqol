package com.github.ob_yekt.simpleqol.mixin.worldgen;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {

    @Redirect(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean reduceLeafLitter(
            WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            int flags
    ) {
        if (state.is(Blocks.LEAF_LITTER)
                && level.getRandom().nextFloat() >= ConfigManager.getLeafLitterMultiplier()) {
            return false;
        }

        return level.setBlock(pos, state, flags);
    }
}