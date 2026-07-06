package net.finnsetchell.winetoggle.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.finnsetchell.winetoggle.WineToggle;
import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class WineToggleFabric implements ModInitializer {

    private static final ResourceKey<CreativeModeTab> VINERY_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("vinery", "vinery"));

    @Override
    public void onInitialize() {
        WineToggle.init();

        // Hide disabled wines from Vinery's creative tab.
        ItemGroupEvents.modifyEntriesEvent(VINERY_TAB).register(entries -> {
            entries.getDisplayStacks().removeIf(WineToggleFabric::isDisabled);
            entries.getSearchTabStacks().removeIf(WineToggleFabric::isDisabled);
        });

        // Make any existing disabled-wine bottle inert (can't be drunk/used).
        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (isDisabled(stack)) {
                return InteractionResultHolder.fail(stack);
            }
            return InteractionResultHolder.pass(stack);
        });
    }

    private static boolean isDisabled(ItemStack stack) {
        return WineToggleConfig.isDisabled(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
