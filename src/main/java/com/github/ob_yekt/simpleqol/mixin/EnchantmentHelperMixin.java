package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Caps the enchanting table's effective bookshelf count, rather than the
 * final cost, so the level cap from {@code maxEnchantingTableLevel} in
 * simpleqol_config.json is enforced while preserving vanilla's roll
 * variance (instead of flattening the top slot to a single fixed number).
 *
 * Vanilla's EnchantmentHelper.getEnchantmentCost(RandomSource, int slot,
 * int bookcases, ItemStack itemStack) clamps bookcases to 15 internally;
 * we clamp it further (down) to whatever ConfigManager computes as the
 * largest bookshelf count whose worst-case roll stays within the
 * configured max level. Every downstream calculation in the method uses
 * this already-capped value, so all three slots scale consistently.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @ModifyVariable(
            method = "getEnchantmentCost",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1 // int-typed args in order: slot (0), bookcases (1)
    )
    private static int simpleqol$capEnchantingBookshelves(int bookcases) {
        int cap = ConfigManager.getMaxEnchantingBookshelves();
        return Math.min(bookcases, cap);
    }
}