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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"))
    private void scheduleDecayOnUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos,
                                       net.minecraft.util.math.Direction direction, BlockPos neighborPos,
                                       BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir) {
        // Get the updated state from the method's return value
        BlockState updatedState = cir.getReturnValue();

        // Check if the block is a leaf eligible for decay (DISTANCE == 7 and not PERSISTENT)
        if (!(Boolean)updatedState.get(Properties.PERSISTENT) && updatedState.get(Properties.DISTANCE_1_7) == 7) {
            if (world instanceof ServerWorld serverWorld) {
                // Calculate tick delay based on multiplier (1.0 = vanilla speed, >1.0 = faster, <1.0 = slower)
                float multiplier = Math.max(0.001f, ConfigManager.getLeafDecayMultiplier());
                int baseDelay = 20; // ~1 second at 20 ticks/second, roughly vanilla's average random tick interval for leaves
                int delay = Math.max(1, Math.round(baseDelay / multiplier));

                // Schedule a block tick for this leaf
                serverWorld.scheduleBlockTick(pos, (Block)(Object)this, delay);
            }
        }
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void handleScheduledDecay(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // If leaf is eligible for decay, decay it now
        if (!(Boolean)state.get(Properties.PERSISTENT) && state.get(Properties.DISTANCE_1_7) == 7) {
            LeavesBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
            ci.cancel(); // Prevent vanilla scheduled tick logic (distance updates), as we've handled decay
        }
    }
}