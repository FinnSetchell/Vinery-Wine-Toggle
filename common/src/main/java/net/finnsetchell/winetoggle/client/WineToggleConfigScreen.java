package net.finnsetchell.winetoggle.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.finnsetchell.winetoggle.WineToggleConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared Cloth Config screen for editing the disabled-wines list in-game. Opened from Mod Menu (Fabric)
 * and from Forge's mod-list config button. Cloth Config's API is identical on both loaders, so this lives
 * in common and is compiled against the API (provided at runtime by each loader).
 */
public final class WineToggleConfigScreen {

    private WineToggleConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("winetoggle.config.title"));

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("winetoggle.config.category"));

        general.addEntry(entries.startStrList(
                        Component.translatable("winetoggle.config.disabledWines"),
                        new ArrayList<>(WineToggleConfig.disabledWines()))
                .setDefaultValue(List.of("vinery:eiswein"))
                .setTooltip(Component.translatable("winetoggle.config.disabledWines.tooltip"))
                .setSaveConsumer(WineToggleConfig::save)
                .build());

        return builder.build();
    }
}
