package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal")
public class EndermanEntityMixin {

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void disableIfConfigured(CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigManager.isEndermanGriefingAllowed()) {
            cir.setReturnValue(false);
        }
    }
}