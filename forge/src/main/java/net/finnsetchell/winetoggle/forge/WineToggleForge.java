package net.finnsetchell.winetoggle.forge;

import net.finnsetchell.winetoggle.WineToggle;
import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;

@Mod(WineToggle.MOD_ID)
public final class WineToggleForge {

    private static final ResourceKey<CreativeModeTab> VINERY_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("vinery", "vinery"));

    public WineToggleForge() {
        WineToggle.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onBuildCreativeTab);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> WineToggleForgeClient::registerConfigScreen);
    }

    // Hide disabled wines from Vinery's creative tab.
    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(VINERY_TAB)) {
            List<ItemStack> toRemove = new ArrayList<>();
            for (var entry : event.getEntries()) {
                if (isDisabled(entry.getKey())) {
                    toRemove.add(entry.getKey());
                }
            }
            toRemove.forEach(event.getEntries()::remove);
        }
    }

    // Make any existing disabled-wine bottle inert (can't be drunk/used).
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isDisabled(event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    private static boolean isDisabled(ItemStack stack) {
        return WineToggleConfig.isDisabled(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}
