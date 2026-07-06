package net.finnsetchell.winetoggle.forge;

import net.finnsetchell.winetoggle.client.WineToggleConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Client-only: registers the Cloth config screen so it opens from Forge's mod list "Config" button.
 * Referenced only via {@code DistExecutor} on the client so its client-side classes never load on a server.
 */
public final class WineToggleForgeClient {

    private WineToggleForgeClient() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> WineToggleConfigScreen.create(parent)));
    }
}
