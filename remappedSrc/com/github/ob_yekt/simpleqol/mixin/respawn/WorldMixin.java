package com.github.ob_yekt.simpleqol.mixin.respawn;

import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(World.class)
public class WorldMixin {

    @Inject(method = "breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;I)Z", at = @At("HEAD"))
    private void onBreakBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        World world = (World)(Object)this;
        if (world.isClient() || !(world.getBlockState(pos).getBlock() instanceof RespawnAnchorBlock)) {
            return;
        }

        // Clear spawn points for all players using this anchor
        Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList().forEach(player -> {
            ServerPlayerEntity.Respawn respawn = player.getRespawn();
            if (respawn != null &&
                    respawn.respawnData().globalPos().pos().equals(pos) &&
                    respawn.respawnData().globalPos().dimension().equals(world.getRegistryKey())) {
                player.setSpawnPoint(null, false);
            }
        });
    }
}