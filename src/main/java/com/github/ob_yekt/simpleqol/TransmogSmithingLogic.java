package com.github.ob_yekt.simpleqol;

import com.github.ob_yekt.simpleqol.ConfigManager;
import com.github.ob_yekt.simpleqol.TransmogRuneUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.Equippable;

import java.util.ArrayList;
import java.util.List;

/**
 * The actual transmogrification logic. This is intentionally NOT a registered
 * Recipe - it's called directly from SmithingMenuMixin, so nothing about it
 * ever needs to be known by a connecting client (no RecipeSerializer entry,
 * no recipe broadcast). The client just sees whatever ItemStack ends up in
 * the result slot, the same as it would for any vanilla smithing recipe.
 *
 * UNVERIFIED: ItemLore's constructor/lines() accessor - going off general
 * knowledge of the post-1.20.5 component system, not a confirmed source for
 * 26.2. Paste ItemLore.java if this doesn't compile.
 */
public final class TransmogSmithingLogic {

    private TransmogSmithingLogic() {}

    public static boolean matches(ItemStack template, ItemStack base, ItemStack addition) {
        if (!ConfigManager.isTransmogrificationEnabled()) return false;
        if (!TransmogRuneUtil.isRune(template)) return false;
        if (!TransmogUtil.isValidTransmogTarget(base)) return false;
        if (!addition.isEmpty()) return false;

        Item disguise = TransmogRuneUtil.getDisguiseItem(template);
        if (disguise == Items.AIR) return false;

        // Easter egg restriction: a Stick rune can only go on swords.
        if (disguise == Items.STICK) {
            return TransmogUtil.isSword(base);
        }

        // Otherwise, the disguise's category has to match the base item's
        // category - no putting a sword's look on a pickaxe, a helmet's
        // look on a chestplate, etc.
        TransmogUtil.Category baseCategory = TransmogUtil.categoryOf(base);
        TransmogUtil.Category disguiseCategory = TransmogUtil.categoryOf(new ItemStack(disguise));
        return baseCategory != TransmogUtil.Category.UNKNOWN && baseCategory == disguiseCategory;
    }

    public static ItemStack assemble(ItemStack template, ItemStack base) {
        ItemStack result = base.copy();
        Item disguise = TransmogRuneUtil.getDisguiseItem(template);
        if (disguise == Items.AIR) {
            return result;
        }

        // Always start from a clean slate so re-transmogrifying never stacks
        // multiple "(Transmogrified to X)" lines.
        stripTransmogLore(result);

        if (disguise == result.getItem()) {
            // Applying an item's own rune to itself undoes the disguise -
            // restore its natural model/equippable explicitly. (Using
            // ItemStack#remove here instead would mark the component as
            // explicitly absent rather than falling back to the item's
            // default - which renders as a blank/missing-texture sprite,
            // not the natural look.)
            ItemStack natural = new ItemStack(result.getItem());
            Identifier naturalModelId = natural.getOrDefault(
                    DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(result.getItem()));
            result.set(DataComponents.ITEM_MODEL, naturalModelId);
            Equippable naturalEquip = natural.get(DataComponents.EQUIPPABLE);
            if (naturalEquip != null) {
                result.set(DataComponents.EQUIPPABLE, naturalEquip);
            }
            return result;
        }

        ItemStack disguiseSample = new ItemStack(disguise);

        // 1) In-hand / inventory / dropped-on-ground appearance.
        Identifier modelId = disguiseSample.getOrDefault(
                DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(disguise));
        result.set(DataComponents.ITEM_MODEL, modelId);

        // 2) Worn-on-body appearance, for armor - copy the base's own
        // Equippable verbatim except for assetId, which is the field that
        // actually controls the worn model.
        Equippable baseEquip = result.get(DataComponents.EQUIPPABLE);
        Equippable disguiseEquip = disguiseSample.get(DataComponents.EQUIPPABLE);
        if (baseEquip != null && disguiseEquip != null && disguiseEquip.assetId().isPresent()) {
            result.set(DataComponents.EQUIPPABLE, new Equippable(
                    baseEquip.slot(),
                    baseEquip.equipSound(),
                    disguiseEquip.assetId(),
                    baseEquip.cameraOverlay(),
                    baseEquip.allowedEntities(),
                    baseEquip.dispensable(),
                    baseEquip.swappable(),
                    baseEquip.damageOnHurt(),
                    baseEquip.equipOnInteract(),
                    baseEquip.canBeSheared(),
                    baseEquip.shearingSound()
            ));
        }

        // 3) Lore line, since the item's real name/type doesn't change -
        // "Netherite Helmet \n (Transmogrified to Chainmail Helmet)".
        List<Component> lines = new ArrayList<>();
        ItemLore existingLore = result.get(DataComponents.LORE);
        if (existingLore != null) {
            lines.addAll(existingLore.lines());
        }
        String disguiseName = disguiseSample.getHoverName().getString();
        lines.add(Component.literal("(Transmogrified to " + disguiseName + ")"));
        result.set(DataComponents.LORE, new ItemLore(lines));

        return result;
    }

    /**
     * Removes any previous "(Transmogrified to X)" line from the stack's
     * lore, leaving any other lore (from other mods/anvil renaming/etc.)
     * untouched. UNVERIFIED: ItemStack#remove(DataComponentType) - inferred
     * by convention alongside the confirmed get()/set()/has(), not directly
     * confirmed from a source you've shown me.
     */
    private static void stripTransmogLore(ItemStack stack) {
        ItemLore existingLore = stack.get(DataComponents.LORE);
        if (existingLore == null) {
            return;
        }

        List<Component> kept = new ArrayList<>();
        for (Component line : existingLore.lines()) {
            String text = line.getString();
            if (!(text.startsWith("(Transmogrified to ") && text.endsWith(")"))) {
                kept.add(line);
            }
        }

        if (kept.isEmpty()) {
            stack.remove(DataComponents.LORE);
        } else {
            stack.set(DataComponents.LORE, new ItemLore(kept));
        }
    }
}
