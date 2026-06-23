package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void simpleqol$removeGriefingGoals(CallbackInfo ci) {

        if (!ConfigManager.isEndermanGriefingAllowed()) {

            ((net.minecraft.world.entity.Mob)(Object)this)
                    .getGoalSelector()
                    .removeAllGoals(goal -> {
                        String name = goal.getClass().getSimpleName();
                        return name.equals("EndermanTakeBlockGoal")
                                || name.equals("EndermanLeaveBlockGoal");
                    });
        }
    }
}