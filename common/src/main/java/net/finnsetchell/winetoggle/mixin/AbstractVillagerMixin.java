package net.finnsetchell.winetoggle.mixin;

import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Removes any merchant offer that sells a disabled wine. Targeting vanilla {@link AbstractVillager} covers
 * every merchant that could sell one — Vinery's winemaker villager and its wandering wine trader both extend
 * it — without needing Vinery on the classpath.
 */
@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

    @Inject(method = "getOffers", at = @At("RETURN"))
    private void winetoggle$filterDisabledWineOffers(CallbackInfoReturnable<MerchantOffers> cir) {
        MerchantOffers offers = cir.getReturnValue();
        if (offers == null || offers.isEmpty()) {
            return;
        }
        offers.removeIf(offer ->
                WineToggleConfig.isDisabled(BuiltInRegistries.ITEM.getKey(offer.getResult().getItem())));
    }
}
