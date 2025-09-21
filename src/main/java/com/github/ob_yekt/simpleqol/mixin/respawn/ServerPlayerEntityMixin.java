package com.github.ob_yekt.simpleqol.mixin.respawn;

import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "getRespawnTarget(ZLnet/minecraft/world/TeleportTarget$PostDimensionTransition;)Lnet/minecraft/world/TeleportTarget;", at = @At("HEAD"), cancellable = true)
    private void customGetRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        ServerPlayerEntity.Respawn respawn = player.getRespawn();
        if (respawn == null) {
            // Fallback to vanilla behavior if no respawn point is set
            return;
        }

        // Get dimension and position from the new respawn data structure
        RegistryKey<World> dimension = respawn.respawnData().globalPos().dimension();
        BlockPos respawnPos = respawn.respawnData().globalPos().pos();

        ServerWorld world = Objects.requireNonNull(player.getEntityWorld().getServer()).getWorld(dimension);
        if (world == null) {
            // Fallback to vanilla behavior if the dimension is invalid
            return;
        }

        BlockState state = world.getBlockState(respawnPos);
        if (state.getBlock() instanceof RespawnAnchorBlock && state.get(RespawnAnchorBlock.CHARGES) > 0) {
            Optional<Vec3d> respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, respawnPos);
            if (respawnPosition.isPresent()) {
                // Get yaw and pitch from respawn data
                float yaw = respawn.respawnData().yaw();
                float pitch = respawn.respawnData().pitch();

                // Return a TeleportTarget to respawn at the anchor's position in its dimension
                cir.setReturnValue(new TeleportTarget(world, respawnPosition.get(), Vec3d.ZERO, yaw, pitch, postDimensionTransition));
                return;
            }
        }

        // If anchor is invalid, reset spawn for all players using this anchor
        if (state.getBlock() instanceof RespawnAnchorBlock) {
            player.getEntityWorld().getServer().getPlayerManager().getPlayerList().forEach(p -> {
                ServerPlayerEntity.Respawn playerRespawn = p.getRespawn();
                if (playerRespawn != null &&
                        playerRespawn.respawnData().globalPos().pos().equals(respawnPos) &&
                        playerRespawn.respawnData().globalPos().dimension().equals(dimension)) {
                    p.setSpawnPoint(null, false);
                }
            });
        }
        // Fallback to vanilla behavior (world spawn) if anchor is invalid
    }
}