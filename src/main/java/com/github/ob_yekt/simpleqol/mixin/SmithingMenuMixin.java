package com.github.ob_yekt.simpleqol.mixin;

import com.github.ob_yekt.simpleqol.TransmogRuneUtil;
import com.github.ob_yekt.simpleqol.TransmogUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * Intercepts the smithing table server-side so transmogrification never needs
 * a registered Recipe/RecipeSerializer (which would require client-side
 * registry sync, breaking vanilla clients). Two things have to happen:
 *
 *   1. Slot 0/1 normally reject anything that isn't part of a REGISTERED
 *      smithing recipe's ingredient set (RecipePropertySet, built from
 *      createInputSlotDefinitions). Since we have no registered recipe, the
 *      rune and most target gear would get rejected before they ever reach
 *      the result computation - so the @Redirect below widens those two
 *      slots' acceptance predicates.
 *   2. createResult() looks up a real Recipe via RecipeManager. We inject at
 *      HEAD and substitute our own computed result when our pattern matches,
 *      cancelling the rest of the method so vanilla's lookup never runs.
 *
 * onTake() in SmithingMenu unconditionally shrinks all three input slots by 1
 * regardless of how the result was produced, so ingredient consumption needs
 * no extra handling here.
 *
 * UNVERIFIED / HIGHEST RISK ITEM: the exact method descriptor in the
 * @Redirect's `target` string for ItemCombinerMenuSlotDefinition$Builder's
 * withSlot(...) overload. I don't have ItemCombinerMenuSlotDefinition.java,
 * so I'm inferring the signature (int, int, int, Predicate<ItemStack>) from
 * how it's called in SmithingMenu's source. If this redirect fails to apply
 * at mixin-load time, paste ItemCombinerMenuSlotDefinition.java and I'll fix
 * the descriptor exactly. Package paths for the inventory-menu classes
 * (net.minecraft.world.inventory) and Inventory
 * (net.minecraft.world.entity.player) are also inferred by long-standing
 * convention, not confirmed from a source you've shown me directly.
 */
@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    // Required so this class compiles against ItemCombinerMenu's only
    // constructor - never actually invoked at runtime, Mixin discards it.
    protected SmithingMenuMixin(
            MenuType<?> menuType,
            int containerId,
            Inventory inventory,
            ContainerLevelAccess access,
            ItemCombinerMenuSlotDefinition itemInputSlots
    ) {
        super(menuType, containerId, inventory, access, itemInputSlots);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void simpleqol$createTransmogResult(CallbackInfo ci) {
        ItemStack template = this.inputSlots.getItem(0);
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        if (com.github.ob_yekt.simpleqol.TransmogSmithingLogic.matches(template, base, addition)) {
            ItemStack result = com.github.ob_yekt.simpleqol.TransmogSmithingLogic.assemble(template, base);
            this.resultSlots.setRecipeUsed(null);
            this.resultSlots.setItem(0, result);
            ci.cancel();
        }
    }

    @Inject(method = "canMoveIntoInputSlots", at = @At("HEAD"), cancellable = true)
    private void simpleqol$allowShiftClickTransmogItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (TransmogRuneUtil.isRune(stack) && !this.getSlot(0).hasItem()) {
            cir.setReturnValue(true);
        } else if (TransmogUtil.isValidTransmogTarget(stack) && !this.getSlot(1).hasItem()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "createInputSlotDefinitions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;"
                            + "withSlot(IIILjava/util/function/Predicate;)"
                            + "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;"
            )
    )
    private static ItemCombinerMenuSlotDefinition.Builder simpleqol$widenSlotAcceptance(
            ItemCombinerMenuSlotDefinition.Builder builder,
            int slotIndex,
            int x,
            int y,
            Predicate<ItemStack> originalTest
    ) {
        Predicate<ItemStack> widened = switch (slotIndex) {
            case 0 -> originalTest.or(TransmogRuneUtil::isRune);
            case 1 -> originalTest.or(TransmogUtil::isValidTransmogTarget);
            default -> originalTest;
        };
        return builder.withSlot(slotIndex, x, y, widened);
    }
}
