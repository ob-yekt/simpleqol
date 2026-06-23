package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin {

    @Shadow
    protected abstract boolean decaying(BlockState state);

    // This is the scheduled (non-random) tick that recalculates DISTANCE
    // after a neighboring log/leaf changes.
    @Inject(method = "tick", at = @At("TAIL"))
    private void simpleqol$fastDecay(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        float multiplier = ConfigManager.getLeafDecayMultiplier();
        if (multiplier <= 1.0f) return; // vanilla speed, do nothing extra

        // distance has just been updated in the world by vanilla's tick() body
        BlockState current = level.getBlockState(pos);
        if (this.decaying(current)) {
            float chance = (multiplier - 1f) / multiplier; // multiplier→∞ means "always instant"
            if (random.nextFloat() < chance) {
                Block.dropResources(current, level, pos);
                level.removeBlock(pos, false);
            }
        }
    }
}