package com.github.ob_yekt.simpleqol.mixin.librarian;

import com.github.ob_yekt.simpleqol.ConfigManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    // Dummy constructor matching AbstractVillager super signature
    public VillagerMixin(net.minecraft.world.entity.EntityType<? extends AbstractVillager> type, net.minecraft.world.level.Level level) {
        super(type, level);
    }

    // Levels 1-4 no longer need any handling here at all: AbstractVillagerMixin
    // strips enchanted-book trades out of the candidate pool BEFORE vanilla
    // samples offers, so the villager naturally ends up with two non-book
    // trades every time. This injection now only handles level 5 (Master),
    // which fully rebuilds its own offers regardless.
    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void adjustLibrarianTrades(ServerLevel level, CallbackInfo ci) {
        if (!ConfigManager.isLibrarianRebalanceEnabled()) return;

        Villager villager = (Villager) (Object) this;
        if (!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) return;

        int villagerLevel = villager.getVillagerData().level();

        if (this.offers != null && villagerLevel == 5) {
            // Remove existing level 5 defaults to overwrite them cleanly
            this.offers.removeIf(offer -> offer.getResult().is(Items.ENCHANTED_BOOK)
                    || offer.getResult().is(Items.DYED_CANDLE.red())
                    || offer.getResult().is(Items.DYED_CANDLE.yellow()));

            // Add the guaranteed custom maxUses = 1 randomized book
            MerchantOffer bookOffer = fabricNerf$createRandomBookOffer(level);
            if (bookOffer != null) {
                this.offers.add(bookOffer);
            }

            // Add 50% chance for a red or yellow candle using proper getter methods
            ItemStack candleStack = new ItemStack(this.random.nextBoolean() ? Items.DYED_CANDLE.red() : Items.DYED_CANDLE.yellow());
            this.offers.add(new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    candleStack,
                    12, 30, 0.05f
            ));
        }
    }

    @Inject(method = "restock", at = @At("TAIL"))
    private void regenerateBookOnRestock(CallbackInfo ci) {
        if (!ConfigManager.isLibrarianRebalanceEnabled()) return;

        Villager villager = (Villager) (Object) this;
        if (!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) return;

        boolean bookOfferChanged = false;

        if (this.offers != null && this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < this.offers.size(); i++) {
                MerchantOffer currentOffer = this.offers.get(i);

                // Swap the old book trade entirely for a freshly rolled one on restock
                if (currentOffer.getResult().is(Items.ENCHANTED_BOOK)) {
                    MerchantOffer freshBookOffer = fabricNerf$createRandomBookOffer(serverLevel);
                    if (freshBookOffer != null) {
                        this.offers.set(i, freshBookOffer);
                        bookOfferChanged = true;
                    }
                }
            }
        }

        // Force the trade screen closed if a player currently has it open,
        // but only when we actually swapped a book trade out from under
        // them. Without this, a player sitting in the menu across a restock
        // could keep buying the freshly re-rolled, maxUses=1 book trade over
        // and over without ever needing to reopen the menu. closeContainer()
        // both notifies the client to close the screen and resets the
        // server-side container state, so they have to reopen it to see
        // the new trade.
        if (bookOfferChanged) {
            Player tradingPlayer = this.getTradingPlayer();
            if (tradingPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.closeContainer();
            }
        }
    }

    @Unique
    private MerchantOffer fabricNerf$createRandomBookOffer(ServerLevel level) {
        var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var enchantments = enchantmentRegistry.listElements().toList();
        if (enchantments.isEmpty()) return null;

        // Roll random enchantment and level
        var randomEnchantment = enchantments.get(this.random.nextInt(enchantments.size()));
        int minLvl = randomEnchantment.value().getMinLevel();
        int maxLvl = randomEnchantment.value().getMaxLevel();
        int finalLvl = minLvl == maxLvl ? minLvl : minLvl + this.random.nextInt(maxLvl - minLvl + 1);

        // Build the modern DataComponent enchanted book stack
        ItemStack bookStack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutableEnchants.set(randomEnchantment, finalLvl);
        bookStack.set(DataComponents.STORED_ENCHANTMENTS, mutableEnchants.toImmutable());

        // Calculate dynamic cost scaling with level
        int emeraldCost = 5 + this.random.nextInt(10) + (finalLvl * 3);
        emeraldCost = Math.min(emeraldCost, 64);

        return new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                Optional.of(new ItemCost(Items.BOOK)),
                bookStack,
                1,      // maxUses = 1 stock limit
                15,     // Villager XP reward
                0.2f    // Price multiplier
        );
    }
}