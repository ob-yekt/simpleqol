package com.github.ob_yekt.simpleqol.mixin.librarian;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

/**
 * Targets AbstractVillager#addOffersFromTradeSet, which is where vanilla
 * resolves a TradeSet's HolderSet<VillagerTrade> pool and randomly samples
 * the configured number of offers (2, for librarians levels 1-4) from it.
 *
 * Instead of letting vanilla pick 2-of-3 (sometimes including the enchanted
 * book) and then stripping the book out afterward -- which can leave the
 * villager with only one trade -- we remove enchanted-book entries from the
 * pool BEFORE sampling happens. Vanilla's own selection logic then naturally
 * fills both slots from whatever's left, no manual backfill required.
 */
@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

    @Redirect(
            method = "addOffersFromTradeSet",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/trading/TradeSet;getTrades()Lnet/minecraft/core/HolderSet;"
            )
    )
    private HolderSet<VillagerTrade> simpleqol$filterLibrarianBookTrades(TradeSet tradeSet) {
        HolderSet<VillagerTrade> trades = tradeSet.getTrades();

        if (!ConfigManager.isLibrarianRebalanceEnabled()) return trades;

        // "this" here is the AbstractVillager instance running updateTrades
        // (e.g. the Villager or WanderingTrader). We only want to touch
        // librarian pools at levels 1-4.
        if (!(((Object) this) instanceof Villager villager)) return trades;
        if (!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) return trades;

        int villagerLevel = villager.getVillagerData().level();
        if (villagerLevel < 1 || villagerLevel > 4) return trades;

        List<Holder<VillagerTrade>> filtered = new ArrayList<>();
        for (Holder<VillagerTrade> holder : trades) {
            if (!simpleqol$isEnchantedBookTrade(holder.value())) {
                filtered.add(holder);
            }
        }

        // Safety net: if every trade in the pool happened to be an enchanted
        // book (shouldn't normally happen), fall back to the unfiltered pool
        // rather than handing back an empty one.
        if (filtered.isEmpty()) return trades;

        return HolderSet.direct(filtered);
    }

    @Unique
    private static boolean simpleqol$isEnchantedBookTrade(VillagerTrade trade) {
        ItemStackTemplate gives = ((VillagerTradeAccessorMixin) trade).simpleqol$getGives();
        return gives.item().value() == Items.ENCHANTED_BOOK;
    }
}