package net.finnsetchell.winetoggle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiny standalone config ({@code config/winetoggle.json}) holding the list of wine item IDs to disable.
 * Read once at mod init; no dependency on Vinery.
 */
public final class WineToggleConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("WineToggle");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static List<String> disabledWines = List.of();

    private WineToggleConfig() {
    }

    public static void load() {
        Path path = Platform.getConfigFolder().resolve("winetoggle.json");
        try {
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    List<String> list = new ArrayList<>();
                    if (root.has("disabled_wines")) {
                        for (JsonElement e : root.getAsJsonArray("disabled_wines")) {
                            list.add(e.getAsString());
                        }
                    }
                    disabledWines = List.copyOf(list);
                }
            } else {
                disabledWines = List.of("vinery:eiswein");
                writeDefault(path);
            }
            LOGGER.info("Loaded {} disabled wine(s): {}", disabledWines.size(), disabledWines);
        } catch (Exception e) {
            LOGGER.error("Failed to load winetoggle config; no wines will be disabled", e);
            disabledWines = List.of();
        }
    }

    private static void writeDefault(Path path) {
        try {
            JsonObject root = new JsonObject();
            JsonArray arr = new JsonArray();
            arr.add("vinery:eiswein");
            root.add("disabled_wines", arr);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, GSON.toJson(root));
        } catch (Exception e) {
            LOGGER.error("Failed to write default winetoggle config", e);
        }
    }

    /** Persists a new disabled-wines list and updates the in-memory value (used by the config screen). */
    public static void save(List<String> wines) {
        disabledWines = List.copyOf(wines);
        Path path = Platform.getConfigFolder().resolve("winetoggle.json");
        try {
            JsonObject root = new JsonObject();
            JsonArray arr = new JsonArray();
            for (String s : disabledWines) {
                arr.add(s);
            }
            root.add("disabled_wines", arr);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, GSON.toJson(root));
            LOGGER.info("Saved {} disabled wine(s): {}", disabledWines.size(), disabledWines);
        } catch (Exception e) {
            LOGGER.error("Failed to save winetoggle config", e);
        }
    }

    public static boolean isDisabled(ResourceLocation id) {
        return id != null && disabledWines.contains(id.toString());
    }

    public static List<String> disabledWines() {
        return disabledWines;
    }
}
