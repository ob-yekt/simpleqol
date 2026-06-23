package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {

    @Redirect(
            method = "entityInside",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean cancelBerryBushDamage(
            Entity entity,
            ServerLevel serverLevel,
            net.minecraft.world.damagesource.DamageSource damageSource,
            float amount
    ) {
        // Only block sweet berry bush damage
        if (!ConfigManager.isSweetBerryBushDamageAllowed() &&
                damageSource == serverLevel.damageSources().sweetBerryBush()) {
            return false; // Prevent damage
        }

        // Allow all other damage
        return entity.hurtServer(serverLevel, damageSource, amount);
    }
}