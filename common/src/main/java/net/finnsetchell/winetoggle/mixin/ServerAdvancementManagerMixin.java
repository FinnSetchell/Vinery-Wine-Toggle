package net.finnsetchell.winetoggle.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

/**
 * Keeps Vinery's {@code wine_collector} advancement completable when a wine is disabled: its single criterion
 * requires holding every wine at once, so we strip disabled wines from it before the advancement is built.
 * Editing the raw JSON at load keeps this config-driven and independent of the Vinery version's wine list.
 */
@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {

    private static final ResourceLocation WINE_COLLECTOR = new ResourceLocation("vinery", "main/wine_collector");

    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD")
    )
    private void winetoggle$patchWineCollector(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        List<String> disabled = WineToggleConfig.disabledWines();
        if (disabled == null || disabled.isEmpty()) {
            return;
        }
        JsonElement element = map.get(WINE_COLLECTOR);
        if (element == null || !element.isJsonObject()) {
            return;
        }
        try {
            JsonObject conditions = element.getAsJsonObject()
                    .getAsJsonObject("criteria")
                    .getAsJsonObject("get_wines")
                    .getAsJsonObject("conditions");
            JsonArray items = conditions.getAsJsonArray("items");
            JsonArray kept = new JsonArray();
            for (JsonElement entry : items) {
                boolean drop = false;
                for (JsonElement id : entry.getAsJsonObject().getAsJsonArray("items")) {
                    if (disabled.contains(id.getAsString())) {
                        drop = true;
                        break;
                    }
                }
                if (!drop) {
                    kept.add(entry);
                }
            }
            conditions.add("items", kept);
        } catch (Exception e) {
            // Advancement structure differs from what we expect (e.g. a future Vinery); leave it untouched.
        }
    }
}
