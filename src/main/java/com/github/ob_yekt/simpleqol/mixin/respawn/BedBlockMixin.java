package com.github.ob_yekt.simpleqol.mixin.respawn;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void preventSpawnSet(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer && !player.isSneaking()) {
            ServerPlayerEntity.Respawn respawn = serverPlayer.getRespawn();
            if (respawn != null && world.getBlockState(respawn.pos()).getBlock() instanceof RespawnAnchorBlock) {
                // Allow sleeping to skip night but don't set spawn
                player.trySleep(pos).ifLeft((reason) -> {
                    if (reason != null) {
                        player.sendMessage(reason.getMessage(), true);
                    }
                }).ifRight(unit -> {
                    // Successfully sleeping, do nothing to prevent spawn point change
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}