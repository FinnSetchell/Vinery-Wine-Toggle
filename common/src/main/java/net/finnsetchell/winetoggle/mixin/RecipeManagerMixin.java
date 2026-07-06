package net.finnsetchell.winetoggle.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Drops any recipe whose result item is a disabled wine, regardless of recipe type. This covers Vinery's
 * fermentation barrel, apple press, and Create mixing (Create's getResultItem returns its first rollable
 * output). JEI/REI/recipe book read the synced RecipeManager, so they hide the recipes for free.
 * Runs server-side at datapack load; the filtered set syncs to clients.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Shadow private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Shadow private Map<ResourceLocation, Recipe<?>> byName;

    private static final Logger WINETOGGLE$LOGGER = LoggerFactory.getLogger("WineToggle");

    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL")
    )
    private void winetoggle$filterDisabledWines(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        List<String> disabled = WineToggleConfig.disabledWines();
        if (disabled == null || disabled.isEmpty()) {
            return;
        }

        int[] removed = {0};

        Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>();
        this.byName.forEach((id, recipe) -> {
            if (winetoggle$isDisabledResult(recipe)) {
                removed[0]++;
            } else {
                newByName.put(id, recipe);
            }
        });

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newByType = new HashMap<>();
        this.recipes.forEach((type, byId) -> {
            Map<ResourceLocation, Recipe<?>> kept = new HashMap<>();
            byId.forEach((id, recipe) -> {
                if (!winetoggle$isDisabledResult(recipe)) {
                    kept.put(id, recipe);
                }
            });
            newByType.put(type, ImmutableMap.copyOf(kept));
        });

        this.byName = ImmutableMap.copyOf(newByName);
        this.recipes = ImmutableMap.copyOf(newByType);

        if (removed[0] > 0) {
            WINETOGGLE$LOGGER.info("Removed {} recipe(s) producing disabled wines: {}", removed[0], disabled);
        }
    }

    private boolean winetoggle$isDisabledResult(Recipe<?> recipe) {
        try {
            ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY);
            if (result == null || result.isEmpty()) {
                return false;
            }
            return WineToggleConfig.isDisabled(BuiltInRegistries.ITEM.getKey(result.getItem()));
        } catch (Throwable t) {
            // A misbehaving third-party recipe must never break datapack loading.
            return false;
        }
    }
}
