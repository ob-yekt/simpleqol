package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin {

    @Shadow
    protected abstract boolean decaying(BlockState state);

    @Inject(method = "tick", at = @At("TAIL"))
    private void simpleqol$instantDecay(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            CallbackInfo ci
    ) {
        if (!ConfigManager.isInstantLeafDecayEnabled()) {
            return;
        }

        BlockState current = level.getBlockState(pos);

        if (this.decaying(current)) {
            Block.dropResources(current, level, pos);
            level.removeBlock(pos, false);

            // Play one break sound occasionally
            if (random.nextInt(24) == 0) {
                level.playSound(
                        null,
                        pos,
                        current.getSoundType().getBreakSound(),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                );
            }
        }
    }
}
