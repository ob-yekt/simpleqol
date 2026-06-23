package com.github.ob_yekt.simpleqol.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Vanilla's Player#causeFallDamage has a hardcoded short-circuit:
 *
 *   if (this.abilities.mayfly) { return false; }
 *
 * This mod keeps mayfly permanently ON for anyone wearing a working elytra
 * (so the normal double-jump-to-fly gesture works), which means that
 * unpatched, vanilla would also exempt them from ALL fall damage at ALL
 * times - even when just walking around, never having flown at all. The
 * event-based ElytraFallDamageHandler never even gets a chance to weigh in,
 * because this check happens before any damage event is fired.
 *
 * This mixin redirects that single field read: the "mayfly" exemption only
 * applies while the player is actually flying right now. Everything else
 * about vanilla's fall damage calculation (feather falling, slow falling,
 * totems, etc.) is left completely untouched.
 */
@Mixin(Player.class)
public abstract class ElytraFallDamageMixin {

    @Redirect(
            method = "causeFallDamage",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean simpleqol$onlyExemptWhileActuallyFlying(Abilities abilities) {
        Player self = (Player) (Object) this;
        if (self instanceof ServerPlayer serverPlayer) {
            return serverPlayer.getAbilities().flying;
        }
        return abilities.mayfly;
    }
}