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
                respawn.pos().equals(pos) &&
                respawn.dimension().equals(world.getRegistryKey());

        if (isPlayerCurrentAnchor) {
            // Player is right-clicking their own active respawn anchor - deactivate it

            // Use a more direct approach to clear the spawn point
            ServerPlayerEntity.Respawn worldSpawn = new ServerPlayerEntity.Respawn(
                    world.getRegistryKey(),
                    world.getSpawnPos(),
                    world.getSpawnAngle(),
                    false // Add the missing boolean argument
            );

            // This should clear the player's custom spawn and revert to world spawn
            try {
                // Try to clear by setting to world spawn instead of null
                serverPlayer.setSpawnPoint(worldSpawn, false);
                // Then immediately clear it completely
                clearPlayerSpawn(serverPlayer);
            } catch (Exception e) {
                System.out.println("Error clearing spawn point: " + e.getMessage());
            }

            // Play sound for deactivation
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 0.8F);

            // Check if any other players are using this same anchor
            boolean otherPlayersUsing = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList().stream()
                    .anyMatch(p -> p != serverPlayer &&
                            p.getRespawn() != null &&
                            p.getRespawn().pos().equals(pos) &&
                            p.getRespawn().dimension().equals(world.getRegistryKey()));

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
                ServerWorld oldWorld = Objects.requireNonNull(player.getEntityWorld().getServer()).getWorld(respawn.dimension());
                if (oldWorld != null && oldWorld.getBlockState(respawn.pos()).getBlock() instanceof RespawnAnchorBlock) {
                    BlockPos oldPos = respawn.pos();

                    // Check if other players are using the old anchor
                    boolean othersUseOld = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList().stream()
                            .anyMatch(p -> p != serverPlayer &&
                                    p.getRespawn() != null &&
                                    p.getRespawn().pos().equals(oldPos) &&
                                    p.getRespawn().dimension().equals(respawn.dimension()));

                    // If no other players use the old anchor, set its charges to 0
                    if (!othersUseOld) {
                        oldWorld.setBlockState(oldPos, oldWorld.getBlockState(oldPos).with(RespawnAnchorBlock.CHARGES, 0), 3);
                    }
                }
            }

            // Set the new spawn point
            ServerPlayerEntity.Respawn newRespawn = new ServerPlayerEntity.Respawn(world.getRegistryKey(), pos, 0.0F, false);
            serverPlayer.setSpawnPoint(newRespawn, false); // Changed to false to avoid op requirement

            // Set the anchor to charged state
            world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, 4), 3);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    // Helper method to clear player spawn using reflection if needed
    @Unique
    private void clearPlayerSpawn(ServerPlayerEntity player) {
        try {
            // Try to access the private field directly
            java.lang.reflect.Field respawnField = ServerPlayerEntity.class.getDeclaredField("respawn");
            respawnField.setAccessible(true);
            respawnField.set(player, null);
        } catch (Exception e) {
            System.out.println("Could not clear spawn via reflection: " + e.getMessage());
        }
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void preventExplosion(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "charge", at = @At("HEAD"), cancellable = true)
    private static void preventChargeChange(CallbackInfo ci) {
        ci.cancel();
    }
}