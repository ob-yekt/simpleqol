package com.github.ob_yekt.simpleqol.mixin.respawn;

import net.minecraft.block.BedBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class BedBlockMixin {

    @Inject(method = "setSpawnPoint(Lnet/minecraft/server/network/ServerPlayerEntity$Respawn;Z)V", at = @At("HEAD"), cancellable = true)
    private void preventBedSpawnPoint(ServerPlayerEntity.Respawn respawn, boolean sendMessage, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        ServerPlayerEntity.Respawn currentRespawn = player.getRespawn();

        // Check if the player has an active Respawn Anchor
        if (currentRespawn != null) {
            // Get dimension and position from the new respawn data structure
            ServerWorld currentWorld = Objects.requireNonNull(player.getEntityWorld().getServer())
                    .getWorld(currentRespawn.respawnData().method_74894()); // Gets dimension

            if (currentWorld != null) {
                BlockPos currentPos = currentRespawn.respawnData().method_74897(); // Gets BlockPos
                if (currentWorld.getBlockState(currentPos).getBlock() instanceof RespawnAnchorBlock) {
                    // Check if the new spawn point is a bed
                    if (respawn != null) {
                        ServerWorld newWorld = player.getEntityWorld().getServer()
                                .getWorld(respawn.respawnData().method_74894());
                        if (newWorld != null) {
                            BlockPos newPos = respawn.respawnData().method_74897();
                            if (newWorld.getBlockState(newPos).getBlock() instanceof BedBlock) {
                                // Prevent setting the spawn point to the bed, but allow sleeping
                                ci.cancel();
                            }
                        }
                    }
                }
            }
        }
    }
}