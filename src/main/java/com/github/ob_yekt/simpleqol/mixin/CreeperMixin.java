package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {

    @Unique
    private boolean simpleqol$isNewSpawn = true;

    protected CreeperMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructorTail(EntityType<? extends Creeper> type, Level level, CallbackInfo ci) {
        // Run on construction. If it's a natural spawn or spawn egg, this rolls.
        if (level != null && !level.isClientSide()) {
            if (this.random.nextFloat() < ConfigManager.getChargedCreeperChance()) {
                Creeper creeper = (Creeper) (Object) this;
                creeper.getEntityData().set(CreeperAccessor.getDATA_IS_POWERED(), true);
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void onReadSavedDataHead(ValueInput input, CallbackInfo ci) {
        // If this method executes, it means the entity is being loaded from disk (server restart / chunk load).
        // If the NBT specifically contains data, we turn off the flag so it doesn't overwrite anything.
        this.simpleqol$isNewSpawn = false;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadSavedDataTail(ValueInput input, CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;
        // If it wasn't a disk load, but rather a spawn egg adding custom NBT on top of a fresh spawn,
        // we enforce our flag state or the saved state.
        if (!creeper.level().isClientSide() && this.simpleqol$isNewSpawn) {
            // It's a fresh spawn egg! Re-apply if it didn't specifically contain a true/false NBT override
            if (!input.contains("powered") && creeper.getRandom().nextFloat() < ConfigManager.getChargedCreeperChance()) {
                creeper.getEntityData().set(CreeperAccessor.getDATA_IS_POWERED(), true);
            }
        }
    }
}