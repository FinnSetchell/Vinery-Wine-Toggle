package net.finnsetchell.winetoggle.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.finnsetchell.winetoggle.client.WineToggleConfigScreen;

@Environment(EnvType.CLIENT)
public final class WineToggleModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return WineToggleConfigScreen::create;
    }
}
