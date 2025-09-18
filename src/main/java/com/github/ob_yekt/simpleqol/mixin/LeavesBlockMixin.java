package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.Direction;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"))
    private void scheduleDecayOnUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos,
                                       Direction direction, BlockPos neighborPos, BlockState neighborState, Random random,
                                       CallbackInfoReturnable<BlockState> cir) {
        // Get the updated state from the method's return value
        BlockState updatedState = cir.getReturnValue();

        // Check if the block is a leaf eligible for decay
        if (!(Boolean)updatedState.get(Properties.PERSISTENT) && updatedState.get(Properties.DISTANCE_1_7) == 7) {
            if (world instanceof ServerWorld serverWorld) {
                // Get the multiplier, ensuring it's at least 1.0
                float multiplier = Math.max(1.0f, ConfigManager.getLeafDecayMultiplier());

                // Skip for multiplier = 1.0 to preserve vanilla behavior
                if (multiplier == 1.0f) {
                    return;
                }

                // Schedule ticks for multiplier >= 1.0 to speed up decay
                int baseDelay = 20; // ~1 second, rough vanilla random tick interval
                int delay = Math.max(1, Math.round(baseDelay / multiplier));
                serverWorld.scheduleBlockTick(pos, (Block)(Object)this, delay);
            }
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void handleScheduledDecay(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {

        // If leaf is eligible for decay
        if (!(Boolean)state.get(Properties.PERSISTENT) && state.get(Properties.DISTANCE_1_7) == 7) {
            float multiplier = Math.max(1.0f, ConfigManager.getLeafDecayMultiplier());


            // Skip for multiplier = 1.0 to preserve vanilla behavior
            if (multiplier == 1.0f) {
                return;
            }

            // Calculate decay chance (vanilla 5%, scaled by multiplier, capped at 1.0)
            float decayChance = Math.min(1.0f, 0.025f * multiplier); // 5% at multiplier = 2.0

            if (random.nextFloat() < decayChance) {
                LeavesBlock.dropStacks(state, world, pos); // Uses loot table, including 5% jungle sapling drop
                world.removeBlock(pos, false);

                // Trigger neighbor updates for adjacent leaves
                float neighborUpdateChance = Math.min(1.0f, 0.2f * multiplier); // 40% at multiplier = 2.0
                for (Direction direction : Direction.values()) {
                    if (random.nextFloat() < neighborUpdateChance) {
                        BlockPos neighborPos = pos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof LeavesBlock) {
                            world.updateNeighbor(neighborPos, (Block)(Object)this, null);
                        }
                    }
                }
            } else {
                // Schedule a tick for neighbors to prevent stragglers
                float neighborTickChance = Math.min(1.0f, 0.05f * multiplier); // 10% at multiplier = 2.0
                for (Direction direction : Direction.values()) {
                    if (random.nextFloat() < neighborTickChance) {
                        BlockPos neighborPos = pos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof LeavesBlock && !(Boolean)neighborState.get(Properties.PERSISTENT) && neighborState.get(Properties.DISTANCE_1_7) == 7) {
                            world.scheduleBlockTick(neighborPos, (Block)(Object)this, Math.max(1, Math.round(20 / multiplier)));
                        }
                    }
                }
            }

            ci.cancel(); // Prevent vanilla scheduled tick logic
        }
    }
}