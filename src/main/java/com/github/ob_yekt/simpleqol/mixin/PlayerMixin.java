package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(
            method = "tryToStartFallFlying",
            at = @At("HEAD"),
            cancellable = true
    )
    private void simpleqol$disableVanillaElytra(
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!ConfigManager.isVanillaElytraEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(
            method = "getFlyingSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"
            )
    )
    private boolean simpleqol$disableSprintFlying(boolean original) {

        Player player = (Player) (Object) this;

        if (!ConfigManager.isVanillaElytraEnabled()
                && player.getAbilities().flying
                && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {

            return false;
        }

        return original;
    }
}