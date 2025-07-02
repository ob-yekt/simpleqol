package com.github.ob_yekt.simpleqol.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.Info.class)
public class SpawnHelperMixin {

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void preventPhantomInLight(EntityType<?> type, BlockPos pos, Chunk chunk, CallbackInfoReturnable<Boolean> cir) {
        if (type == EntityType.PHANTOM && chunk instanceof WorldChunk worldChunk) {
            ServerWorld world = (ServerWorld) worldChunk.getWorld(); // Cast to ServerWorld
            // Check nighttime only in Overworld
            if (world.getRegistryKey().equals(World.OVERWORLD) && !world.isNight()) {
                cir.setReturnValue(false); // Cancel spawn if not nighttime in Overworld
                return;
            }
            // Check block light level
            int blockLight = world.getLightLevel(LightType.BLOCK, pos);
            if (blockLight > 0) {
                cir.setReturnValue(false); // Cancel spawn if block light level > 0
            }
        }
    }
}