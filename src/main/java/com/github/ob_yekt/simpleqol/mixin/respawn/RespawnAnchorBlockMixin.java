package com.github.ob_yekt.simpleqol.mixin.respawn;

import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void customOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            cir.setReturnValue(ActionResult.CONSUME);
            return;
        }

        ServerPlayerEntity.Respawn respawn = serverPlayer.getRespawn();

        // Check if this specific anchor is the player's current spawn point
        boolean isPlayerCurrentAnchor = respawn != null &&
                respawn.respawnData().globalPos().pos().equals(pos) &&
                respawn.respawnData().globalPos().dimension().equals(world.getRegistryKey());

        if (isPlayerCurrentAnchor) {
            // Player is right-clicking their own active respawn anchor - deactivate it

            // Clear the player's spawn point - try direct method first
            try {
                serverPlayer.setSpawnPoint(null, false);
            } catch (Exception e) {
                clearPlayerSpawn(serverPlayer);
            }

            // Play sound for deactivation
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 0.8F);

            // Check if any other players are using this same anchor
            boolean otherPlayersUsing = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList().stream()
                    .anyMatch(p -> p != serverPlayer &&
                            p.getRespawn() != null &&
                            p.getRespawn().respawnData().globalPos().pos().equals(pos) &&
                            p.getRespawn().respawnData().globalPos().dimension().equals(world.getRegistryKey()));

            // If no other players are using it, set charges to 0
            if (!otherPlayersUsing) {
                world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, 0), 3);
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            // Player is activating a new respawn anchor

            // If player already has a respawn anchor, handle the old one
            if (respawn != null) {
                ServerWorld oldWorld = Objects.requireNonNull(player.getEntityWorld().getServer())
                        .getWorld(respawn.respawnData().globalPos().dimension());

                if (oldWorld != null) {
                    BlockPos oldPos = respawn.respawnData().globalPos().pos();
                    if (oldWorld.getBlockState(oldPos).getBlock() instanceof RespawnAnchorBlock) {
                        // Check if other players are using the old anchor
                        boolean othersUseOld = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList().stream()
                                .anyMatch(p -> p != serverPlayer &&
                                        p.getRespawn() != null &&
                                        p.getRespawn().respawnData().globalPos().pos().equals(oldPos) &&
                                        p.getRespawn().respawnData().globalPos().dimension().equals(respawn.respawnData().globalPos().dimension()));

                        // If no other players use the old anchor, set its charges to 0
                        if (!othersUseOld) {
                            oldWorld.setBlockState(oldPos, oldWorld.getBlockState(oldPos).with(RespawnAnchorBlock.CHARGES, 0), 3);
                        }
                    }
                }
            }

            // Create new respawn data using the proper constructor
            try {
                // Create the respawn data using the static factory method
                WorldProperties.SpawnPoint respawnData = WorldProperties.SpawnPoint.create(
                        world.getRegistryKey(),
                        pos,
                        0.0F, // yaw
                        0.0F  // pitch
                );

                // Create the respawn object
                ServerPlayerEntity.Respawn newRespawn = new ServerPlayerEntity.Respawn(respawnData, false);

                serverPlayer.setSpawnPoint(newRespawn, false);

            } catch (Exception e) {
                // Silently handle the error
            }

            // Set the anchor to charged state
            world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, 4), 3);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    // Helper method to clear player spawn using reflection
    @Unique
    private void clearPlayerSpawn(ServerPlayerEntity player) {
        try {
            // Try multiple possible field names
            String[] possibleFieldNames = {"respawn", "field_13996", "spawnPoint", "respawnPoint"};

            for (String fieldName : possibleFieldNames) {
                try {
                    java.lang.reflect.Field respawnField = ServerPlayerEntity.class.getDeclaredField(fieldName);
                    respawnField.setAccessible(true);
                    respawnField.set(player, null);
                    return; // Success, exit the method
                } catch (NoSuchFieldException ignored) {
                }
            }

            // If all field names failed, try to use setSpawnPoint with null
            player.setSpawnPoint(null, false);

        } catch (Exception e) {
            // Silently fail
        }
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void preventExplosion(BlockState state, ServerWorld world, BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "charge", at = @At("HEAD"), cancellable = true)
    private static void preventChargeChange(CallbackInfo ci) {
        ci.cancel();
    }
}