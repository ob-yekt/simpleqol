package com.github.ob_yekt.simpleqol;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

/**
 * Category checks for what can become a rune / what a rune can be applied to.
 *
 * Confirmed against Item.java: ItemStack#has(DataComponentType) exists. Confirmed
 * against Equippable.java: slot() is the correct accessor (it's a record field).
 * ItemStack#is(Item)/#is(TagKey<Item>) are assumed by convention but not directly
 * confirmed.
 */
public final class TransmogUtil {

    private TransmogUtil() {}

    public static boolean isTool(ItemStack stack) {
        return stack.has(DataComponents.TOOL);
    }

    public static boolean isSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
    }

    /**
     * Spears appear to be a distinct weapon type in this version (no Tool
     * component - they use KineticWeapon/PiercingWeapon instead), so they
     * needed their own check. UNVERIFIED: if vanilla Tridents also carry
     * KINETIC_WEAPON under whatever unification happened here, this would
     * also match tridents - let me know if that's the case and I'll narrow
     * it further (e.g. by also checking PIERCING_WEAPON, or excluding
     * Items.TRIDENT explicitly).
     */
    public static boolean isSpear(ItemStack stack) {
        return stack.has(DataComponents.KINETIC_WEAPON);
    }

    public static boolean isArmorPiece(ItemStack stack) {
        Equippable eq = stack.get(DataComponents.EQUIPPABLE);
        if (eq == null) return false;
        EquipmentSlot slot = eq.slot();
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST
                || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean isStick(ItemStack stack) {
        return stack.is(Items.STICK);
    }

    public static boolean isValidRuneSource(ItemStack stack) {
        return isTool(stack) || isSword(stack) || isSpear(stack) || isArmorPiece(stack) || isStick(stack);
    }

    public static boolean isValidTransmogTarget(ItemStack stack) {
        return isTool(stack) || isSword(stack) || isSpear(stack) || isArmorPiece(stack);
    }

    /** Equipment category, used to enforce "pickaxe runes only fit pickaxes" etc. */
    public enum Category {
        PICKAXE, AXE, SHOVEL, HOE, SWORD, SPEAR, HELMET, CHESTPLATE, LEGGINGS, BOOTS, UNKNOWN
    }

    public static Category categoryOf(ItemStack stack) {
        if (stack.is(ItemTags.PICKAXES)) return Category.PICKAXE;
        if (stack.is(ItemTags.AXES)) return Category.AXE;
        if (stack.is(ItemTags.SHOVELS)) return Category.SHOVEL;
        if (stack.is(ItemTags.HOES)) return Category.HOE;
        if (stack.is(ItemTags.SWORDS)) return Category.SWORD;
        if (isSpear(stack)) return Category.SPEAR;

        Equippable eq = stack.get(DataComponents.EQUIPPABLE);
        if (eq != null) {
            return switch (eq.slot()) {
                case HEAD -> Category.HELMET;
                case CHEST -> Category.CHESTPLATE;
                case LEGS -> Category.LEGGINGS;
                case FEET -> Category.BOOTS;
                default -> Category.UNKNOWN;
            };
        }
        return Category.UNKNOWN;
    }
}
