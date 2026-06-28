package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.TransmogRuneUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.KnowledgeBookItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables the vanilla "learn recipes from NBT" right-click behavior
 * specifically when the held Knowledge Book is one of our transmog runes -
 * a rune is meant to be a disguised tool for the smithing table, not a
 * functioning Knowledge Book. Any plain, non-rune Knowledge Book a player
 * somehow obtains keeps its normal vanilla behavior untouched.
 *
 * UNVERIFIED: the package (net.minecraft.world.item.KnowledgeBookItem) is
 * inferred by convention - not confirmed from a source you've shown me. The
 * use(Level, Player, InteractionHand) signature itself IS confirmed (it's the
 * exact override point of Item#use, seen directly in the Item.java you
 * pasted earlier), so if the import resolves, this should attach correctly.
 */
@Mixin(KnowledgeBookItem.class)
public abstract class KnowledgeBookItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void simpleqol$preventRuneUse(
            Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (TransmogRuneUtil.isRune(stack)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
