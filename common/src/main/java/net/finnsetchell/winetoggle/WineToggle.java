package net.finnsetchell.winetoggle;

public final class WineToggle {

    public static final String MOD_ID = "winetoggle";

    private WineToggle() {
    }

    public static void init() {
        WineToggleConfig.load();
    }
}
