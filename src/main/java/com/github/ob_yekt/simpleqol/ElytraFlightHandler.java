package com.github.ob_yekt.simpleqol;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraFlightHandler {

    public static void tick(ServerPlayer player) {

        if (ConfigManager.isVanillaElytraEnabled())
            return;

        if (player.isCreative() || player.isSpectator())
            return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

        boolean hasWorkingElytra =
                chest.is(Items.ELYTRA)
                        && chest.getDamageValue() < chest.getMaxDamage() - 1;

        if (hasWorkingElytra) {
            boolean changed = false;
            if (!player.getAbilities().mayfly) { player.getAbilities().mayfly = true; changed = true; }

            // Use config for custom elytra flight speed
            float targetSpeed = ConfigManager.getCustomElytraFlyingSpeed();
            if (player.getAbilities().getFlyingSpeed() != targetSpeed) {
                player.getAbilities().setFlyingSpeed(targetSpeed);
                changed = true;
            }

            if (changed) player.onUpdateAbilities();

            // Kill the sprint-boost multiplier while this custom flight is active
            if (player.getAbilities().flying && player.isSprinting()) {
                player.setSprinting(false);
            }

            // Only drain durability while actually flying, not just standing around equipped
            if (player.getAbilities().flying && player.tickCount % 20 == 0) {
                int damage = chest.getDamageValue();
                if (damage < chest.getMaxDamage() - 1) {
                    chest.setDamageValue(damage + 1);
                }
            }

        } else {

            boolean changed = false;

            if (player.getAbilities().flying) {
                player.getAbilities().flying = false;
                changed = true;
            }

            if (player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                changed = true;
            }

            if (player.getAbilities().getFlyingSpeed() != 0.05f) {
                player.getAbilities().setFlyingSpeed(0.05f);
                changed = true;
            }

            if (changed) {
                player.onUpdateAbilities();
            }
        }
    }
}