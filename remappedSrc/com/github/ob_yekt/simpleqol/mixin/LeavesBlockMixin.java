package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"))
    private void scheduleCustomDecayTick(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos,
                                         Direction direction, BlockPos neighborPos, BlockState neighborState,
                                         Random random, CallbackInfoReturnable<BlockState> cir) {

        if (world instanceof ServerWorld serverWorld) {
            BlockState returnedState = cir.getReturnValue();

            // Check if this block should start decaying (distance became 7 and not persistent)
            boolean shouldStartDecaying = !(Boolean)returnedState.get(Properties.PERSISTENT) &&
                    returnedState.get(Properties.DISTANCE_1_7) == 7 &&
                    state.get(Properties.DISTANCE_1_7) != 7; // Only if distance just changed to 7

            if (shouldStartDecaying) {
                // Schedule our custom decay tick instead of letting vanilla handle it
                float multiplier = Math.max(0.001f, ConfigManager.getLeafDecayMultiplier());

                // Base delay equivalent to vanilla random tick frequency
                // Vanilla random ticks happen roughly every 68.27 seconds on average for a single block
                // We'll use 20-100 seconds as our base range
                int baseDelayMin = 400; // 20 seconds
                int baseDelayMax = 2000; // 100 seconds
                int baseDelay = baseDelayMin + random.nextInt(baseDelayMax - baseDelayMin);

                int customDelay = Math.max(1, Math.round(baseDelay / multiplier));

                // Schedule with a custom delay - we'll use a slightly different delay to differentiate from vanilla
                serverWorld.scheduleBlockTick(pos, (Block)(Object)this, customDelay);
            }
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void handleCustomDecayTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // Check if this is a decay-eligible leaf block
        if (!(Boolean)state.get(Properties.PERSISTENT) && state.get(Properties.DISTANCE_1_7) == 7) {
            // This is our custom decay tick - decay immediately
            LeavesBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
            ci.cancel(); // Cancel vanilla scheduled tick behavior
            return;
        }

        // If it's not decay-eligible, let vanilla handle the distance update
        // (this happens when distance needs to be recalculated)
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void disableVanillaRandomDecay(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // Disable vanilla random tick decay entirely - we handle it with scheduled ticks
        if (!(Boolean)state.get(Properties.PERSISTENT) && state.get(Properties.DISTANCE_1_7) == 7) {
            ci.cancel(); // Don't let vanilla decay happen via random ticks
        }
    }
}