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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void customOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            cir.setReturnValue(ActionResult.CONSUME);
            return;
        }

        ServerPlayerEntity.Respawn respawn = serverPlayer.getRespawn();
        boolean isAnchorSpawn = respawn != null && serverPlayer.getServer().getWorld(respawn.dimension()) != null && serverPlayer.getServer().getWorld(respawn.dimension()).getBlockState(respawn.pos()).getBlock() instanceof RespawnAnchorBlock;

        if (isAnchorSpawn && respawn.pos().equals(pos) && respawn.dimension().equals(world.getRegistryKey())) {
            // Disable anchor for this player
            serverPlayer.setSpawnPoint(null, false);
            // Check if any other player is using this anchor
            boolean otherPlayersUsing = world.getServer().getPlayerManager().getPlayerList().stream()
                    .anyMatch(p -> p != serverPlayer && p.getRespawn() != null && p.getRespawn().pos().equals(pos) && p.getRespawn().dimension().equals(world.getRegistryKey()));
            if (!otherPlayersUsing) {
                world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, 0), 3);
                world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            // Set spawn point to this anchor
            if (isAnchorSpawn) {
                // Reset old anchor's charge to 0 if no other players use it
                ServerWorld oldWorld = serverPlayer.getServer().getWorld(respawn.dimension());
                if (oldWorld != null) {
                    BlockPos oldPos = respawn.pos();
                    boolean othersUseOld = world.getServer().getPlayerManager().getPlayerList().stream()
                            .anyMatch(p -> p != serverPlayer && p.getRespawn() != null && p.getRespawn().pos().equals(oldPos) && p.getRespawn().dimension().equals(respawn.dimension()));
                    if (!othersUseOld && oldWorld.getBlockState(oldPos).getBlock() instanceof RespawnAnchorBlock) {
                        oldWorld.setBlockState(oldPos, oldWorld.getBlockState(oldPos).with(RespawnAnchorBlock.CHARGES, 0), 3);
                    }
                }
            }
            ServerPlayerEntity.Respawn newRespawn = new ServerPlayerEntity.Respawn(world.getRegistryKey(), pos, 0.0F, false);
            serverPlayer.setSpawnPoint(newRespawn, true);
            world.setBlockState(pos, state.with(RespawnAnchorBlock.CHARGES, 4), 3);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            cir.setReturnValue(ActionResult.SUCCESS);
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