package com.github.ob_yekt.simpleqol.mixin.librarian;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.trading.VillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * VillagerTrade keeps its "gives" template private with no getter, so we
 * need an accessor mixin to read it (we need to know what item a trade
 * produces WITHOUT calling getOffer(), which would burn a roll off the
 * shared RandomSource and desync the real trade sampling).
 */
@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessorMixin {

    @Accessor("gives")
    ItemStackTemplate simpleqol$getGives();
}