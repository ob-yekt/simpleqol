package com.github.ob_yekt.simpleqol.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("TAIL"))
    private void onSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            BlockState oldState = world.getBlockState(pos);
            Block block = state.getBlock();
            Block oldBlock = oldState.getBlock();
            if (block == Blocks.TORCHFLOWER || oldBlock == Blocks.TORCHFLOWER ||
                    block == Blocks.TORCHFLOWER_CROP || oldBlock == Blocks.TORCHFLOWER_CROP ||
                    block == Blocks.POTTED_TORCHFLOWER || oldBlock == Blocks.POTTED_TORCHFLOWER ||
                    block == Blocks.PITCHER_PLANT || oldBlock == Blocks.PITCHER_PLANT ||
                    block == Blocks.PITCHER_CROP || oldBlock == Blocks.PITCHER_CROP ||
                    block == Blocks.OPEN_EYEBLOSSOM || oldBlock == Blocks.OPEN_EYEBLOSSOM ||
                    block == Blocks.CLOSED_EYEBLOSSOM || oldBlock == Blocks.CLOSED_EYEBLOSSOM ||
                    block == Blocks.POTTED_OPEN_EYEBLOSSOM || oldBlock == Blocks.POTTED_OPEN_EYEBLOSSOM ||
                    block == Blocks.POTTED_CLOSED_EYEBLOSSOM || oldBlock == Blocks.POTTED_CLOSED_EYEBLOSSOM) {
                // Force server-side light update
                world.getLightingProvider().checkBlock(pos);
                // Mark block for client-side update
                serverWorld.getChunkManager().markForUpdate(pos);
            }
        }
    }
}