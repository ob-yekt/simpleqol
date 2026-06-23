package com.github.ob_yekt.simpleqol.mixin.anvil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;
    @Shadow private String itemName;
    @Shadow private boolean onlyRenaming;

    @Unique
    private boolean simpleqol$freeRepairOrRename;

    public AnvilMenuMixin(MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinitions) {
        super(type, containerId, playerInventory, access, slotDefinitions);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void simpleqol$handleFreeRepairOrRename(CallbackInfo ci) {
        this.simpleqol$freeRepairOrRename = false;

        ItemStack input = this.inputSlots.getItem(0);
        ItemStack addition = this.inputSlots.getItem(1);

        if (input.isEmpty() || !EnchantmentHelper.canStoreEnchantments(input)) {
            return;
        }

        boolean isMaterialRepair = !addition.isEmpty() && input.isDamageableItem() && input.isValidRepairItem(addition);
        boolean isPureRename = addition.isEmpty() && this.simpleqol$wantsNameChange(input);

        if (!isMaterialRepair && !isPureRename) {
            return;
        }

        ItemStack result = input.copy();
        int materialsUsed = 0;
        boolean hasNameChange = this.simpleqol$wantsNameChange(input);

        if (isMaterialRepair) {
            int repairAmount = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
            if (repairAmount <= 0) {
                // If it's already at full durability but we just want to rename it using materials sitting in slot 2
                if (hasNameChange) {
                    isMaterialRepair = false; // Degrade down to a pure rename action instead
                } else {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    this.repairItemCountCost = 0;
                    this.onlyRenaming = false;
                    ci.cancel();
                    return;
                }
            } else {
                while (repairAmount > 0 && materialsUsed < addition.getCount()) {
                    result.setDamageValue(result.getDamageValue() - repairAmount);
                    materialsUsed++;
                    repairAmount = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                }
            }
        }

        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(input.getHoverName().getString())) {
                result.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if (input.has(DataComponents.CUSTOM_NAME)) {
            result.remove(DataComponents.CUSTOM_NAME);
        }

        result.set(DataComponents.REPAIR_COST, input.getOrDefault(DataComponents.REPAIR_COST, 0));

        this.repairItemCountCost = materialsUsed;
        this.onlyRenaming = !isMaterialRepair;
        this.simpleqol$freeRepairOrRename = true;

        // --- THE FIX ---
        // Dynamically compute the cost: 1 level per material used. If a pure rename, it costs 1.
        int finalLevelCost = isMaterialRepair ? materialsUsed : 1;

        this.resultSlots.setItem(0, result);
        this.cost.set(finalLevelCost);
        this.broadcastChanges();
        ci.cancel();
    }

    @Unique
    private boolean simpleqol$wantsNameChange(ItemStack input) {
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            return !this.itemName.equals(input.getHoverName().getString());
        }
        return input.has(DataComponents.CUSTOM_NAME);
    }

    @Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"))
    private void simpleqol$skipAnvilDamage(ContainerLevelAccess instance, BiConsumer<Level, BlockPos> consumer) {
        if (this.simpleqol$freeRepairOrRename) {
            instance.execute((level, pos) -> level.levelEvent(1030, pos, 0));
        } else {
            instance.execute(consumer);
        }
    }
}