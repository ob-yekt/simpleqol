package com.github.ob_yekt.simpleqol.elytra;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FluidState;

public class ElytraFlightHandler {

    public static void tick(ServerPlayer player) {
        if (ConfigManager.isVanillaElytraEnabled()) return;
        if (player.isCreative() || player.isSpectator()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean hasWorkingElytra = chest.is(Items.ELYTRA)
                && chest.getDamageValue() < chest.getMaxDamage() - 1;

        boolean changed = false;

        if (hasWorkingElytra) {
            // Never allow vanilla elytra gliding. Flight is handled entirely via
            // mayfly + the normal double-jump toggle below instead.
            if (player.isFallFlying()) {
                player.stopFallFlying();
            }

            // Mayfly stays ON whenever a working elytra is equipped so the
            // normal creative-style double-jump toggle works from anywhere
            // (standing or falling). On its own this would also make vanilla
            // skip fall damage entirely — that side effect is patched out by
            // ElytraFallDamageMixin, which only keeps the exemption while the
            // player is actually flying.
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                changed = true;
            }

            float targetSpeed = ConfigManager.getCustomElytraFlyingSpeed();
            if (player.getAbilities().getFlyingSpeed() != targetSpeed) {
                player.getAbilities().setFlyingSpeed(targetSpeed);
                changed = true;
            }

            if (changed) player.onUpdateAbilities();

            if (player.getAbilities().flying && player.isSprinting()) {
                player.setSprinting(false);
            }

            // Durability only while actually flying
            if (player.getAbilities().flying && player.tickCount % 20 == 0) {
                int damage = chest.getDamageValue();
                if (damage < chest.getMaxDamage() - 1) {
                    chest.setDamageValue(damage + 1);
                }
            }

            // Reset fall distance ONLY while flying
            if (player.getAbilities().flying) {
                player.fallDistance = 0.0f;
            }

            // Prevent flying underwater
            if (player.getAbilities().flying && isSubmerged(player)) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }

        } else {
            // Disable
            if (player.getAbilities().flying || player.getAbilities().mayfly) {
                player.getAbilities().flying = false;
                player.getAbilities().mayfly = false;
                changed = true;
            }
            if (player.getAbilities().getFlyingSpeed() != 0.05f) {
                player.getAbilities().setFlyingSpeed(0.05f);
                changed = true;
            }
            if (changed) player.onUpdateAbilities();

            if (player.isFallFlying()) {
                player.stopFallFlying();
            }
        }
    }

    private static boolean isSubmerged(ServerPlayer player) {
        // Check if eyes are in water (or other fluid you want to block)
        FluidState fluidState = player.level().getFluidState(player.blockPosition().above((int) (player.getEyeHeight() - 0.1)));
        return !fluidState.isEmpty();
    }
}