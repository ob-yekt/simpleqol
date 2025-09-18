package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.Direction;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("LeavesBlockMixin");

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
                LOGGER.debug("Multiplier at {}: {}", pos, multiplier);

                // Skip for multiplier = 1.0 to preserve vanilla behavior
                if (multiplier == 1.0f) {
                    LOGGER.debug("Multiplier is 1.0, using vanilla decay behavior at {}", pos);
                    return;
                }

                // Schedule ticks for multiplier >= 1.0 to speed up decay
                int baseDelay = 20; // ~1 second, rough vanilla random tick interval
                int delay = Math.max(1, Math.round(baseDelay / multiplier));
                LOGGER.debug("Scheduling tick at {} with delay {}", pos, delay);
                serverWorld.scheduleBlockTick(pos, (Block)(Object)this, delay);
            }
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void handleScheduledDecay(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        LOGGER.debug("Scheduled tick triggered for leaf at {}", pos);

        // If leaf is eligible for decay
        if (!(Boolean)state.get(Properties.PERSISTENT) && state.get(Properties.DISTANCE_1_7) == 7) {
            float multiplier = Math.max(1.0f, ConfigManager.getLeafDecayMultiplier());
            LOGGER.debug("Multiplier at {}: {}", pos, multiplier);

            // Skip for multiplier = 1.0 to preserve vanilla behavior
            if (multiplier == 1.0f) {
                LOGGER.debug("Multiplier is 1.0, using vanilla decay behavior at {}", pos);
                return;
            }

            // Calculate decay chance (vanilla 5%, scaled by multiplier, capped at 1.0)
            float decayChance = Math.min(1.0f, 0.025f * multiplier); // 5% at multiplier = 2.0
            LOGGER.debug("Decay chance at {}: {}", pos, decayChance);

            if (random.nextFloat() < decayChance) {
                LOGGER.info("Decaying leaf at {} with multiplier {}", pos, multiplier);
                LeavesBlock.dropStacks(state, world, pos); // Uses loot table, including 5% jungle sapling drop
                world.removeBlock(pos, false);

                // Trigger neighbor updates for adjacent leaves
                float neighborUpdateChance = Math.min(1.0f, 0.2f * multiplier); // 40% at multiplier = 2.0
                LOGGER.debug("Neighbor update chance at {}: {}", pos, neighborUpdateChance);
                for (Direction direction : Direction.values()) {
                    if (random.nextFloat() < neighborUpdateChance) {
                        BlockPos neighborPos = pos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof LeavesBlock) {
                            LOGGER.debug("Triggering neighbor update for {} from decay at {}", neighborPos, pos);
                            world.updateNeighbor(neighborPos, (Block)(Object)this, null);
                        }
                    }
                }
            } else {
                // Schedule a tick for neighbors to prevent stragglers
                float neighborTickChance = Math.min(1.0f, 0.05f * multiplier); // 10% at multiplier = 2.0
                LOGGER.debug("No decay at {}, scheduling neighbor ticks with chance {}", pos, neighborTickChance);
                for (Direction direction : Direction.values()) {
                    if (random.nextFloat() < neighborTickChance) {
                        BlockPos neighborPos = pos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof LeavesBlock && !(Boolean)neighborState.get(Properties.PERSISTENT) && neighborState.get(Properties.DISTANCE_1_7) == 7) {
                            LOGGER.debug("Scheduling tick for neighbor at {} with delay {}", neighborPos, Math.max(1, Math.round(20 / multiplier)));
                            world.scheduleBlockTick(neighborPos, (Block)(Object)this, Math.max(1, Math.round(20 / multiplier)));
                        }
                    }
                }
            }

            ci.cancel(); // Prevent vanilla scheduled tick logic
        }
    }
}