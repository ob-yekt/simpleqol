package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.TimeController;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class SleepMixin {

    @Inject(method = "wakeUpAllPlayers", at = @At("TAIL"))
    private void onWakeSleepingPlayers(CallbackInfo ci) {
        TimeController.resetTime();
    }
}