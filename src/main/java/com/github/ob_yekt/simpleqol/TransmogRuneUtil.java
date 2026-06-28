package com.github.ob_yekt.simpleqol;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

/**
 * The rune is now just a plain Knowledge Book with a marker tucked into the
 * vanilla minecraft:custom_data component (under our own namespaced key, to
 * avoid colliding with anything else that might use custom_data) - no custom
 * Item, no custom DataComponentType, so nothing here needs client-side
 * registration. The recipe JSONs bake the matching custom_name + custom_data
 * directly into the result, so crafting a rune needs zero Java code at all -
 * this class is only for reading it back later, during smithing application.
 *
 * Confirmed against CompoundTag.java: getCompound()/getString() return
 * Optional<T> in this version (not the old default-value style), so this
 * reads them via flatMap rather than null/contains() checks.
 */
public final class TransmogRuneUtil {

    private static final String ROOT_KEY = "simpleqol_transmog";
    private static final String DISGUISE_KEY = "disguise";

    private TransmogRuneUtil() {}

    public static boolean isRune(ItemStack stack) {
        return stack.is(Items.KNOWLEDGE_BOOK) && getDisguiseId(stack).isPresent();
    }

    public static Optional<Identifier> getDisguiseId(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return Optional.empty();
        }
        CompoundTag tag = data.copyTag();
        return tag.getCompound(ROOT_KEY)
                .flatMap(root -> root.getString(DISGUISE_KEY))
                .flatMap(s -> Optional.ofNullable(Identifier.tryParse(s)));
    }

    public static Item getDisguiseItem(ItemStack runeStack) {
        return getDisguiseId(runeStack)
                .flatMap(id -> BuiltInRegistries.ITEM.get(id).map(Holder::value))
                .orElse(Items.AIR);
    }
}
