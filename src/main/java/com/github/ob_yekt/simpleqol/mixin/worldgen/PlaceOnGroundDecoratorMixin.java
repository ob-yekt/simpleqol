package com.github.ob_yekt.simpleqol.mixin.worldgen;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(PlaceOnGroundDecorator.class)
public class PlaceOnGroundDecoratorMixin {

    @Redirect(
            method = "attemptToPlaceBlockAbove(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private void reduceDecoratorLeafLitter(TreeDecorator.Context context, BlockPos pos, BlockState state) {
        if (state.is(Blocks.LEAF_LITTER)) {
            double config = ConfigManager.getLeafLitterMultiplier();
            // Cube the config value to drastically lower tree generation density
            // while preserving 0.0 as 0% and 1.0 as 100%
            double scaledChance = Math.pow(config, 3);

            if (ThreadLocalRandom.current().nextFloat() >= scaledChance) {
                return;
            }
        }

        context.setBlock(pos, state);
    }
}