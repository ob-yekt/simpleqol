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
            ServerWorld currentWorld = Objects.requireNonNull(player.getEntityWorld().getServer()).getWorld(currentRespawn.dimension());
            if (currentWorld != null && currentWorld.getBlockState(currentRespawn.pos()).getBlock() instanceof RespawnAnchorBlock) {
                // Check if the new spawn point is a bed (respawn parameter is not null and is a bed)
                if (respawn != null) {
                    ServerWorld newWorld = player.getEntityWorld().getServer().getWorld(respawn.dimension());
                    if (newWorld != null && newWorld.getBlockState(respawn.pos()).getBlock() instanceof BedBlock) {
                        // Prevent setting the spawn point to the bed, but allow sleeping (handled elsewhere)
                        ci.cancel();
                    }
                }
            }
        }
    }
}